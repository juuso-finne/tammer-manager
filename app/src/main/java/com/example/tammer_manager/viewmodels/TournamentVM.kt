package com.example.tammer_manager.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
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
        newList.add(RegisteredPlayer(
            fullName = player.fullName,
            rating = player.rating,
            id = getNextPlayerId()
        ))
        savedStateHandle["registeredPlayers"] = newList.toList()
    }

    fun removePlayer(index: Int){
        val newList = registeredPlayers.value.toMutableList()
        val roundsCompleted = activeTournament.value?.roundsCompleted ?: 0
        if(roundsCompleted > 0){
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

    private fun addToMatchHistory(id: Int, item: MatchHistoryItem){
        val playerList = registeredPlayers.value.toMutableList()
        val index = playerList.indexOfFirst { it.id == id }
        playerList[index] = playerList[index].let { it.copy(matchHistory = it.matchHistory.plusElement(item)) }
    }

    private fun addToMatchHistory(id: Int, item: MatchHistoryItem){
        val playerList = registeredPlayers.value.toMutableList()
        val index = playerList.indexOfFirst { it.id == id }
        playerList[index] = playerList[index].let { it.copy(matchHistory = it.matchHistory.plusElement(item)) }
    }

    fun clearPairings(){
        savedStateHandle["currentRoundPairings"] = listOf<Pairing>()
    }

    fun generatePairs(){
        savedStateHandle["currentRoundPairings"] = generateSwissPairs(registeredPlayers.value.filter { it.isActive })
    }

    fun setPairingScore(index: Int, playerColor: PlayerColor, points: Float){
        val pairingList = currentRoundPairings.value.toMutableList()
        val pairing = pairingList[index].toMutableMap()
        pairing[playerColor] = pairing[playerColor]?.copy(points = points) ?: HalfPairing()

        pairingList[index] = pairing
        savedStateHandle["currentRoundPairings"] = pairingList
    }
}