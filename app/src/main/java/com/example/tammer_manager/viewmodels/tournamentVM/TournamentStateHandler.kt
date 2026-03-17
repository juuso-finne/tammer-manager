package com.example.tammer_manager.viewmodels.tournamentVM

import androidx.lifecycle.SavedStateHandle
import com.example.tammer_manager.TPN_ASSIGNMENT_CUTOFF
import com.example.tammer_manager.data.export_import.ImportedPlayer
import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.MatchHistoryItem
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.classes.PairingList
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.data.tournament_admin.enums.TieBreak
import com.example.tammer_manager.data.tournament_admin.enums.TournamentType
import com.example.tammer_manager.data.tournament_admin.pairing.generateRoundRobinPairs
import com.example.tammer_manager.data.tournament_admin.pairing.swiss.generateSwissPairs
import com.example.tammer_manager.viewmodels.TournamentVMState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.List

class TournamentStateHandler(
    private val savedStateHandle: SavedStateHandle,
    private val vmStateHandler: VMStateHandler,
    private val playerStateHandler: PlayerStateHandler,
    private val viewModelScope: CoroutineScope
) {

    fun clearTournament(){
        savedStateHandle["tournament"] = null
        savedStateHandle["registeredPlayers"] = listOf<RegisteredPlayer>()
        savedStateHandle["nextPlayerId"] = 0
        savedStateHandle["currentRoundPairings"] = listOf<RegisteredPlayer>()
        savedStateHandle["filename"] = ""
        savedStateHandle["isGrouped"] = false
        savedStateHandle["currentGroup"] = ""
        savedStateHandle["groupMap"] = mapOf<String, TournamentVMState>()
    }

    fun initateTournament(
        name: String,
        maxRounds: Int,
        type: TournamentType,
        tieBreaks: List<TieBreak>
    ){
        clearTournament()
        savedStateHandle["tournament"] = Tournament(
            name = name,
            maxRounds = maxRounds,
            type = type,
            tieBreaks = tieBreaks
        )
    }

    fun splitTournament(
        updatedPlayerList: List<RegisteredPlayer>
    ){
        savedStateHandle["isGrouped"] = true
        val groupMap = updatedPlayerList.associateBy(
            keySelector = {updatedPlayer -> updatedPlayer.group},
            valueTransform = {updatedPlayer -> vmStateHandler.getVMState().copy(
                registeredPlayers = updatedPlayerList.filter{it.group == updatedPlayer.group},
                currentGroup = updatedPlayer.group
            )}
        )

        savedStateHandle["groupMap"] = groupMap

        val newGroup = groupMap.keys.sorted()[0]
        savedStateHandle["currentGroup"] = newGroup
        vmStateHandler.setVMState(groupMap[newGroup]!!)
        updateMaxRounds()
    }

    fun switchGroup(
        newGroup: String
    ){
        val groupMap = savedStateHandle.get<Map<String, TournamentVMState>>("groupMap")!!
        val newGroupMap = groupMap.toMutableMap()
        val currentGroup = savedStateHandle.get<String>("currentGroup")!!

        newGroupMap[currentGroup] = vmStateHandler.getVMState()
        savedStateHandle["groupMap"] = newGroupMap

        vmStateHandler.setVMState(groupMap[newGroup]!!)
        updateMaxRounds()
    }

    fun addPlayer(player: ImportedPlayer){
        val newList = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!.toMutableList()
        val tpn = (newList.maxOfOrNull { it.tpn } ?: 0) + 1
        newList.add(
            RegisteredPlayer(
                fullName = player.fullName,
                rating = player.rating,
                id = getNextPlayerId(),
                tpn = tpn
            )
        )
        savedStateHandle["registeredPlayers"] = newList.toList()
        updateMaxRounds()
    }

    fun removePlayer(index: Int){
        val isPaired = savedStateHandle.get<PairingList>("currentRoundPairings")!!.isNotEmpty()
        val newList = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!.toMutableList()
        val hasRecord = !newList[index].matchHistory.isEmpty()
        if(hasRecord || isPaired){
            newList[index] = newList[index].copy(isActive = false)
        }else{
            newList.removeAt(index)
        }
        savedStateHandle["registeredPlayers"] = newList.toList()
        updateMaxRounds()
    }

    private fun getNextPlayerId(): Int{
        savedStateHandle["nextPlayerId"] = savedStateHandle.get<Int>("nextPlayerId")!! + 1
        return savedStateHandle.get<Int>("nextPlayerId")!!
    }

    private fun updateMaxRounds(){

        val activeTournament = savedStateHandle.get<Tournament>("tournament")
        val registeredPlayers = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")

        if(activeTournament?.type == TournamentType.SWISS){
            return
        }

        val playerCount = registeredPlayers!!.size
        val newMax = playerCount - 1 + playerCount % 2

        savedStateHandle["tournament"] = activeTournament?.copy(
            maxRounds = newMax
        )
    }

    fun clearPairings(){
        savedStateHandle["currentRoundPairings"] = listOf<Pairing>()
    }

    private fun advanceRound(){
        val activeTournament = savedStateHandle.get<Tournament>("tournament")!!
        val oldValue = activeTournament.roundsCompleted
        savedStateHandle["tournament"] = activeTournament.copy(roundsCompleted = oldValue + 1)
    }

    fun finishRound(){
        val currentRoundPairings = savedStateHandle.get<PairingList>("currentRoundPairings")!!
        val registeredPlayers = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!
        advanceRound()
        val activeTournament = savedStateHandle.get<Tournament>("tournament")!!

        currentRoundPairings.forEach { pairing ->
            val round = activeTournament.roundsCompleted

            if(
                pairing[PlayerColor.BLACK]?.playerID != null &&
                activeTournament.type == TournamentType.SWISS
            ){
                val white = registeredPlayers
                    .find { player -> player.id == pairing[PlayerColor.WHITE]!!.playerID }

                val black = registeredPlayers
                    .find { player -> player.id == pairing[PlayerColor.BLACK]!!.playerID }

                if(white!!.score > black!!.score){
                    playerStateHandler.addUpfloat(black.id, round)
                    playerStateHandler.addDownfloat(white.id, round)
                } else if(black.score > white.score){
                    playerStateHandler.addUpfloat(white.id, round)
                    playerStateHandler.addDownfloat(black.id, round)
                }
            }

            pairing.forEach { item ->
                val ownColor = item.key

                item.value.playerID?.let{
                    val newItem = MatchHistoryItem(
                        opponentId = pairing[ownColor.reverse()]?.playerID,
                        round = round,
                        result = item.value.points ?: 0f,
                        color = ownColor,
                    )
                    if(newItem.opponentId == null){
                        playerStateHandler.setPlayerReceivedBye(it)
                    }
                    playerStateHandler.addToPlayerScore(id = it, amount = item.value.points ?: 0f)
                    playerStateHandler.addToMatchHistory(id = it, item = newItem)
                }
            }
        }

        clearPairings()
    }

    fun editRound(round: Int, newResults: PairingList){
        newResults.forEach { pairing ->
            pairing.forEach { item ->
                val ownColor = item.key
                item.value.playerID?.let{
                    val newItem = MatchHistoryItem(
                        opponentId = pairing[ownColor.reverse()]?.playerID,
                        round = round,
                        result = item.value.points ?: 0f,
                        color = ownColor,
                    )
                    playerStateHandler.removeFromMatchHistory(playerId = it, round = round, color = ownColor)
                    playerStateHandler.addToMatchHistory(id = it, item = newItem)
                    playerStateHandler.addToPlayerScore(id = it, amount = item.value.points ?: 0f)
                }
            }
        }
    }

    fun setPairs(newPairs: PairingList){
        savedStateHandle["currentRoundPairings"] = newPairs
    }

    fun generatePairs(
        onError: () -> Unit,
        onSuccess: () -> Unit
    ){
        viewModelScope.launch {
            val newPairs = mutableListOf<Pairing>()
            val activeTournament = savedStateHandle.get<Tournament>("tournament")!!
            val registeredPlayers = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!

            if(activeTournament.type == TournamentType.SWISS){
                val success = withContext(Dispatchers.Default) {
                    if ((activeTournament.roundsCompleted) < TPN_ASSIGNMENT_CUTOFF) {
                        playerStateHandler.assignTpns()
                    }

                    generateSwissPairs(
                        players = registeredPlayers.filter { it.isActive },
                        roundsCompleted = activeTournament.roundsCompleted,
                        maxRounds = activeTournament.maxRounds,
                        output = newPairs
                    ).also { ok ->
                        if (ok) {
                            savedStateHandle["currentRoundPairings"] = newPairs
                        }
                    }
                }

                if (success) onSuccess() else onError()
            } else{
                withContext(Dispatchers.Default) {
                    generateRoundRobinPairs(
                        players = registeredPlayers.sortedByDescending { it.rating },
                        output = newPairs,
                        roundsCompleted = activeTournament.roundsCompleted,
                        doubleRoundRobin = activeTournament.type == TournamentType.DOUBLE_ROUND_ROBIN
                    )
                }
                savedStateHandle["currentRoundPairings"] = newPairs
                onSuccess()
            }

        }
    }

    fun setPairingScore(index: Int, playerColor: PlayerColor, points: Float){
        val pairingList = savedStateHandle.get<PairingList>("currentRoundPairings")!!.toMutableList()
        val pairing = pairingList[index].toMutableMap()
        pairing[playerColor] = pairing[playerColor]?.copy(points = points) ?: HalfPairing()

        pairingList[index] = pairing
        savedStateHandle["currentRoundPairings"] = pairingList
    }

    fun reconstructPairings(round: Int): PairingList{
        val registeredPlayers = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!

        return com.example.tammer_manager.data.tournament_admin.reconstructPairings(
            players = registeredPlayers,
            round = round
        )
    }
}