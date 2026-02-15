package com.example.tammer_manager.data.tournament_admin.classes

import android.os.Parcelable
import com.example.tammer_manager.data.tournament_admin.enums.TournamentType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tournament (
    var name: String,
    val maxRounds: Int,
    val type: TournamentType,
    val doubleRoundRobin: Boolean = false,
    var roundsCompleted: Int = 0
): Parcelable