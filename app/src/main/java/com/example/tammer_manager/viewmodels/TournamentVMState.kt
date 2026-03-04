package com.example.tammer_manager.viewmodels

import android.os.Parcelable
import com.example.tammer_manager.data.tournament_admin.classes.PairingList
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class TournamentVMState(
    val tournament: Tournament,
    val registeredPlayers: List<RegisteredPlayer>,
    val nextPlayerId: Int,
    val currentRoundPairings: PairingList,
    val isGrouped: Boolean,
    val currentGroup: String,
): Parcelable
