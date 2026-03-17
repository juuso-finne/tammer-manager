package com.example.tammer_manager.data.tournament_admin.classes

import android.os.Parcelable
import com.example.tammer_manager.data.tournament_admin.enums.TieBreak
import com.example.tammer_manager.data.tournament_admin.enums.TournamentType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Tournament (
    var name: String,
    val maxRounds: Int,
    val type: TournamentType,
    var roundsCompleted: Int = 0,
    val tieBreaks: List<TieBreak>
): Parcelable