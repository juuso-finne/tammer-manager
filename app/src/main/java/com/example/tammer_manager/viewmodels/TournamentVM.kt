package com.example.tammer_manager.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tammer_manager.TPN_ASSIGNMENT_CUTOFF
import com.example.tammer_manager.data.file_management.listTournaments
import com.example.tammer_manager.data.file_management.saveTournament
import com.example.tammer_manager.data.player_import.ImportedPlayer
import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.MatchHistoryItem
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.classes.PairingList
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.data.tournament_admin.enums.TournamentType
import com.example.tammer_manager.data.tournament_admin.pairing.generateRoundRobinPairs
import com.example.tammer_manager.data.tournament_admin.pairing.swiss.generateSwissPairs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TournamentViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    val activeTournament: StateFlow<Tournament?> = savedStateHandle.getStateFlow(
        key = "tournament",
        initialValue =  null
    )
    val registeredPlayers: StateFlow<List<RegisteredPlayer>> = savedStateHandle.getStateFlow(
        key = "registeredPlayers",
        initialValue = listOf()
    )

    val nextPlayerId: StateFlow<Int> = savedStateHandle.getStateFlow(
        key = "nextPlayerId",
        initialValue = 0
    )

    val currentRoundPairings: StateFlow<PairingList> = savedStateHandle.getStateFlow(
        key = "currentRoundPairings",
        initialValue = listOf()
    )

    val filename: StateFlow<String> = savedStateHandle.getStateFlow(
        key = "filename",
        initialValue = ""
    )

    fun clearTournament(){
        savedStateHandle["tournament"] = null
        savedStateHandle["registeredPlayers"] = listOf<RegisteredPlayer>()
        savedStateHandle["nextPlayerId"] = 0
        savedStateHandle["currentRoundPairings"] = listOf<RegisteredPlayer>()
        savedStateHandle["fileName"] = ""
    }

    fun initateTournament(
        name: String,
        maxRounds: Int,
        type: TournamentType,
        doubleRoundRobin: Boolean = false
    ){
        clearTournament()
        savedStateHandle["tournament"] = Tournament(name, maxRounds, type, doubleRoundRobin)
    }

    private fun advanceRound(){
        val oldValue = activeTournament.value?.roundsCompleted ?: 0
        savedStateHandle["tournament"] = activeTournament.value?.copy(roundsCompleted = oldValue + 1)
    }

    private fun updateMaxRounds(){
        if(activeTournament.value?.type == TournamentType.SWISS){
            return
        }

        val playerCount = registeredPlayers.value.size
        val newMax = playerCount - 1 + playerCount % 2

        savedStateHandle["tournament"] = activeTournament.value?.copy(
            maxRounds = newMax
        )
    }

    fun finishRound(){
        advanceRound()

        currentRoundPairings.value.forEach { pairing ->
            pairing.forEach { item ->
                val ownColor = item.key
                item.value.playerID?.let{
                    val newItem = MatchHistoryItem(
                        opponentId = pairing[ownColor.reverse()]?.playerID,
                        round = activeTournament.value?.roundsCompleted ?: 0,
                        result = item.value.points ?: 0f,
                        color = ownColor,
                    )
                    if(newItem.opponentId == null){
                        setPlayerReceivedBye(it)
                    }
                    addToPlayerScore(id = it, amount = item.value.points ?: 0f)
                    addToMatchHistory(id = it, item = newItem)
                }
            }
        }

        clearPairings()
    }

    fun editRound(round: Int, newResults: PairingList){
        val players = registeredPlayers.value
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
                    removeFromMatchHistory(playerId = it, round = round, color = ownColor)
                    addToMatchHistory(id = it, item = newItem)
                    addToPlayerScore(id = it, amount = item.value.points ?: 0f)
                }
            }
        }
    }

    private fun getNextPlayerId(): Int{
        savedStateHandle["nextPlayerId"] = nextPlayerId.value + 1
        return nextPlayerId.value
    }

    fun alteringPlayerCountAllowed(): Boolean{
        return(
            activeTournament.value?.type == TournamentType.SWISS ||
            (
                activeTournament.value?.roundsCompleted == 0 &&
                currentRoundPairings.value.isEmpty()
            )
        )
    }

    fun addPlayer(player: ImportedPlayer){
        val newList = registeredPlayers.value.toMutableList()
        val tpn = (newList.maxOfOrNull { it.tpn } ?: 0) + 1
        newList.add(RegisteredPlayer(
            fullName = player.fullName,
            rating = player.rating,
            id = getNextPlayerId(),
            tpn = tpn
        ))
        savedStateHandle["registeredPlayers"] = newList.toList()
        updateMaxRounds()
    }

    fun findPlayerById(id: Int): RegisteredPlayer?{
        return registeredPlayers.value.find(){it.id == id}
    }

    fun removePlayer(index: Int){
        val newList = registeredPlayers.value.toMutableList()
        val hasRecord = !newList[index].matchHistory.isEmpty()
        if(hasRecord || isPaired()){
            newList[index] = newList[index].copy(isActive = false)
        }else{
            newList.removeAt(index)
        }
        savedStateHandle["registeredPlayers"] = newList.toList()
        updateMaxRounds()
    }

    fun activatePlayer(index: Int){
        val newList = registeredPlayers.value.toMutableList()
        newList[index] = newList[index].copy(isActive = true)
        savedStateHandle["registeredPlayers"] = newList.toList()
    }

    fun addToPlayerScore(id: Int, amount: Float){
        val playerList = registeredPlayers.value.toMutableList()
        val index = playerList.indexOfFirst { it.id == id }
        playerList[index] = playerList[index].let { it.copy(score = it.score + amount) }
        savedStateHandle["registeredPlayers"] = playerList.toList()
    }

    fun setPlayerReceivedBye(id: Int, value:Boolean = true){
        val playerList = registeredPlayers.value.toMutableList()
        val index = playerList.indexOfFirst { it.id == id }
        playerList[index] = playerList[index].copy(receivedPairingBye = value)
        savedStateHandle["registeredPlayers"] = playerList.toList()
    }

    fun assignTpns(){
        val newList = registeredPlayers.value.sortedByDescending { it.rating }.toMutableList()
        for(i in 0..<newList.size){
            newList[i] = newList[i].copy(tpn = i + 1)
        }
        savedStateHandle["registeredPlayers"] = newList.toList()
    }

    private fun addToMatchHistory(id: Int, item: MatchHistoryItem){
        val playerList = registeredPlayers.value.toMutableList()
        val index = playerList.indexOfFirst { it.id == id }
        playerList[index] = playerList[index].let { it.copy(matchHistory = it.matchHistory.plusElement(item)) }
        savedStateHandle["registeredPlayers"] = playerList.toList()
    }

    private fun removeFromMatchHistory(playerId: Int, round: Int, color: PlayerColor){
        val playerList = registeredPlayers.value.toMutableList()
        val index = playerList.indexOfFirst { it.id == playerId }
        val oldScore = playerList[index].matchHistory.find { it.round == round && it.color == color}?.result ?: 0f
        playerList[index] = playerList[index].let { it.copy(matchHistory = it.matchHistory.filterNot { match ->
            match.round == round && match.color == color
        }) }
        savedStateHandle["registeredPlayers"] = playerList.toList()
        addToPlayerScore(id = playerId, amount = -oldScore)
    }

    fun isPaired(): Boolean{
        return !currentRoundPairings.value.isEmpty()
    }

    fun clearPairings(){
        savedStateHandle["currentRoundPairings"] = listOf<Pairing>()
    }

    fun generatePairs(
        onError: () -> Unit,
        onSuccess: () -> Unit
    ){
        viewModelScope.launch {
            val newPairs = mutableListOf<Pairing>()

            if(activeTournament.value?.type == TournamentType.SWISS){
                val success = withContext(Dispatchers.Default){
                    if ((activeTournament.value?.roundsCompleted ?: 0) < TPN_ASSIGNMENT_CUTOFF) {
                        assignTpns()
                    }

                    generateSwissPairs(
                        players = registeredPlayers.value.filter { it.isActive },
                        roundsCompleted = activeTournament.value?.roundsCompleted ?: 0,
                        maxRounds = activeTournament.value?.maxRounds ?: 0,
                        output = newPairs
                    ).also{ ok ->
                        if(ok){
                            savedStateHandle["currentRoundPairings"] = newPairs
                        }
                    }
                }

                if (success) onSuccess() else onError()
            } else{
                withContext(Dispatchers.Default){
                    generateRoundRobinPairs(
                        players = registeredPlayers.value.sortedByDescending { it.rating },
                        output = newPairs,
                        roundsCompleted = activeTournament.value?.roundsCompleted ?: 0,
                        doubleRoundRobin = activeTournament.value?.doubleRoundRobin ?: false
                    )
                }
                savedStateHandle["currentRoundPairings"] = newPairs
                onSuccess()
            }

        }
    }

    fun setPairingScore(index: Int, playerColor: PlayerColor, points: Float){
        val pairingList = currentRoundPairings.value.toMutableList()
        val pairing = pairingList[index].toMutableMap()
        pairing[playerColor] = pairing[playerColor]?.copy(points = points) ?: HalfPairing()

        pairingList[index] = pairing
        savedStateHandle["currentRoundPairings"] = pairingList
    }

    fun save(
        context: Context,
        onError: () -> Unit
    ){

        if (filename.value.isEmpty()){
            throw Exception("Filename cannot be empty string")
        }

        val data = TournamentVMState(
            activeTournament = activeTournament.value!!,
            registeredPlayers = registeredPlayers.value,
            nextPlayerId = nextPlayerId.value,
            currentRoundPairings = currentRoundPairings.value
        )

        if(saveTournament(
            context = context,
            data = data,
            filename = filename.value
        )){
            return
        }
        onError()
    }

    fun saveAs(
        newFilename: String,
        context: Context,
        onError: () -> Unit,
        overWrite: Boolean = false,
        confirmOverWrite: () -> Unit = {}
    ){

        if (newFilename.isEmpty()){
            throw Exception("Filename cannot be empty string")
        }

        val files = listTournaments(context)
        if(newFilename in files && !overWrite){
            confirmOverWrite()
            return
        }

        if (filename.value.isEmpty()){
            savedStateHandle["filename"] = newFilename
        }

        save(
            context = context,
            onError = onError
        )
    }
}