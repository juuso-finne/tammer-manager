package com.example.tammer_manager.data.tournament_admin.classes

import android.os.Parcelable
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class MatchHistoryItem(
    val opponentId: Int?,
    val round: Int,
    val result: Float,
    val color: PlayerColor
): Parcelable

typealias MatchHistory = List<MatchHistoryItem>