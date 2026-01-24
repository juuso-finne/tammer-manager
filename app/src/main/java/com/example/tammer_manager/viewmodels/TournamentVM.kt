package com.example.tammer_manager.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.tammer_manager.TPN_ASSIGNMENT_CUTOFF
import com.example.tammer_manager.data.player_import.ImportedPlayer
import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.MatchHistoryItem
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.classes.PairingList
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.data.tournament_admin.pairing.generateSwissPairs
import kotlinx.coroutines.flow.StateFlow

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

    fun initateTournament(name: String, maxRounds: Int){
        savedStateHandle["tournament"] = Tournament(name, maxRounds)
    }

    private fun advanceRound(){
        val oldValue = activeTournament.value?.roundsCompleted ?: 0
        savedStateHandle["tournament"] = activeTournament.value?.copy(roundsCompleted = oldValue + 1)
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

    private fun getNextPlayerId(): Int{
        savedStateHandle["nextPlayerId"] = nextPlayerId.value + 1
        return nextPlayerId.value
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

    fun isPaired(): Boolean{
        return !currentRoundPairings.value.isEmpty()
    }

    fun clearPairings(){
        savedStateHandle["currentRoundPairings"] = listOf<Pairing>()
    }

    fun generatePairs(onError: () -> Unit){
        if ((activeTournament.value?.roundsCompleted ?: 0) < TPN_ASSIGNMENT_CUTOFF){
            assignTpns()
        }

        val newPairs = mutableListOf<Pairing>()

        if (generateSwissPairs(
            players = registeredPlayers.value.filter{ it.isActive },
            roundsCompleted = activeTournament.value?.roundsCompleted ?: 0,
            maxRounds = activeTournament.value?.maxRounds ?: 0,
            output = newPairs
        )){
            savedStateHandle["currentRoundPairings"] = newPairs
            return
        }

        onError()
    }

    fun setPairingScore(index: Int, playerColor: PlayerColor, points: Float){
        val pairingList = currentRoundPairings.value.toMutableList()
        val pairing = pairingList[index].toMutableMap()
        pairing[playerColor] = pairing[playerColor]?.copy(points = points) ?: HalfPairing()

        pairingList[index] = pairing
        savedStateHandle["currentRoundPairings"] = pairingList
    }
}