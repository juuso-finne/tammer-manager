package com.example.tammer_manager.viewmodels.tournamentVM

import androidx.lifecycle.SavedStateHandle
import com.example.tammer_manager.data.tournament_admin.classes.MatchHistoryItem
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor

class PlayerStateHandler(private val savedStateHandle: SavedStateHandle) {


    fun activatePlayer(index: Int){
        val newList = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!.toMutableList()
        newList[index] = newList[index].copy(isActive = true)
        savedStateHandle["registeredPlayers"] = newList.toList()
    }

    fun addToPlayerScore(id: Int, amount: Float){
        val playerList = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!.toMutableList()
        val index = playerList.indexOfFirst { it.id == id }
        playerList[index] = playerList[index].let { it.copy(score = it.score + amount) }
        savedStateHandle["registeredPlayers"] = playerList.toList()
    }

    fun setPlayerReceivedBye(id: Int, value:Boolean = true){
        val playerList = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!.toMutableList()
        val index = playerList.indexOfFirst { it.id == id }
        playerList[index] = playerList[index].copy(receivedPairingBye = value)
        savedStateHandle["registeredPlayers"] = playerList.toList()
    }

    fun addDownfloat(playerId: Int, round:Int){
        val playerList = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!.toMutableList()
        val index = playerList.indexOfFirst { it.id == playerId }
        playerList[index] = playerList[index].let { it.copy(downfloats = it.downfloats.plus(round)) }
        savedStateHandle["registeredPlayers"] = playerList.toList()
    }

    fun addUpfloat(playerId: Int, round:Int){
        val playerList = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!.toMutableList()
        val index = playerList.indexOfFirst { it.id == playerId }
        playerList[index] = playerList[index].let { it.copy(upfloats = it.upfloats.plus(round)) }
        savedStateHandle["registeredPlayers"] = playerList.toList()
    }

    fun assignTpns(){
        val newList = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!.sortedByDescending { it.rating }.toMutableList()
        for(i in 0..<newList.size){
            newList[i] = newList[i].copy(tpn = i + 1)
        }
        savedStateHandle["registeredPlayers"] = newList.toList()
    }

    fun addToMatchHistory(id: Int, item: MatchHistoryItem){
        val playerList = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!.toMutableList()
        val index = playerList.indexOfFirst { it.id == id }
        playerList[index] = playerList[index].let { it.copy(matchHistory = it.matchHistory.plusElement(item)) }
        savedStateHandle["registeredPlayers"] = playerList.toList()
    }

    fun removeFromMatchHistory(playerId: Int, round: Int, color: PlayerColor){
        val playerList = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!.toMutableList()
        val index = playerList.indexOfFirst { it.id == playerId }
        val oldScore = playerList[index].matchHistory.find { it.round == round && it.color == color}?.result ?: 0f
        playerList[index] = playerList[index].let { it.copy(matchHistory = it.matchHistory.filterNot { match ->
            match.round == round && match.color == color
        }) }
        savedStateHandle["registeredPlayers"] = playerList.toList()
        addToPlayerScore(id = playerId, amount = -oldScore)
    }
}