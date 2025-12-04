package com.example.tammer_manager.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.tammer_manager.data.player_import.ImportedPlayer
import com.example.tammer_manager.data.tournament_admin.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.Tournament
import kotlinx.coroutines.flow.StateFlow

class TournamentViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    val activeTournament: StateFlow<Tournament?> = savedStateHandle.getStateFlow(
        key = "tournament",
        initialValue =  null
    )
    val registeredPlayers:StateFlow<List<RegisteredPlayer>> = savedStateHandle.getStateFlow(
        key ="registeredPlayers",
        initialValue = listOf()
    )

    fun initateTournament(name: String, maxRounds: Int){
        savedStateHandle["tournament"] = Tournament(name, maxRounds)
    }

    fun addPlayer(player: ImportedPlayer){
        val newList = registeredPlayers.value.toMutableList()
        newList.add(RegisteredPlayer(fullName = player.fullName, rating = player.rating))
        savedStateHandle["registeredPlayers"] = newList.toList()
    }

    fun removePlayer(index: Int){
        val newList = registeredPlayers.value.toMutableList()
        val roundsCompleted = activeTournament.value?.roundsCompleted ?: 0
        if(roundsCompleted > 0){
            savedStateHandle["registeredPlayers"] = newList.mapIndexed { i, p ->
                if(i == index) p.copy(isActive = false) else p
            }
            return
        }
        newList.removeAt(index)
        savedStateHandle["registeredPlayers"] = newList.toList()
    }

    fun activatePlayer(index: Int){
        val newList = registeredPlayers.value.toMutableList()
        savedStateHandle["registeredPlayers"] = newList.mapIndexed { i, p ->
            if(i == index) p.copy(isActive = true) else p
        }
    }
}