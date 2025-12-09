package com.example.tammer_manager.data.tournament_admin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisteredPlayer (
    val fullName: String,
    val rating: Int,
    var isActive: Boolean = true,
    var score: Float = 0f,
    var tpn: Int? = null
): Parcelable