package com.example.tammer_manager.data.tournament_admin.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisteredPlayer (
    val fullName: String,
    val rating: Int,
    val id: Int,
    var tpn: Int,
    var isActive: Boolean = true,
    var score: Float = 0f,
    val matchHistory: MatchHistory = listOf()
): Parcelable