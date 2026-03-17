package com.example.tammer_manager.viewmodels.tournamentVM

import androidx.lifecycle.SavedStateHandle
import com.example.tammer_manager.data.tournament_admin.classes.PairingList
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import com.example.tammer_manager.viewmodels.TournamentVMState

class VMStateHandler(private val savedStateHandle: SavedStateHandle) {
    fun getVMState(): TournamentVMState {
        return TournamentVMState(
            tournament = savedStateHandle.get<Tournament>("tournament")!!,
            registeredPlayers = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!,
            nextPlayerId = savedStateHandle.get<Int>("nextPlayerId")!!,
            currentRoundPairings = savedStateHandle.get<PairingList>("currentRoundPairings")!!,
            isGrouped = savedStateHandle.get<Boolean>("isGrouped")!!,
            currentGroup = savedStateHandle.get<String>("currentGroup")!!,
        )
    }

    fun setVMState(data: TournamentVMState){
        savedStateHandle["tournament"] = data.tournament
        savedStateHandle["registeredPlayers"] = data.registeredPlayers
        savedStateHandle["nextPlayerId"] = data.nextPlayerId
        savedStateHandle["currentRoundPairings"] = data.currentRoundPairings
        savedStateHandle["isGrouped"] = data.isGrouped
        savedStateHandle["currentGroup"] = data.currentGroup
    }
}