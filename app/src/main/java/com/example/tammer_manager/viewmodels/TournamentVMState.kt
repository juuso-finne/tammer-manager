package com.example.tammer_manager.viewmodels

import com.example.tammer_manager.data.tournament_admin.classes.PairingList
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import kotlinx.serialization.Serializable

@Serializable
data class TournamentVMState(
    val activeTournament: Tournament,
    val registeredPlayers: List<RegisteredPlayer>,
    val nextPlayerId: Int,
    val currentRoundPairings: PairingList
)
