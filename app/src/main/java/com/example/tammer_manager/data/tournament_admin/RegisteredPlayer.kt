package com.example.tammer_manager.data.tournament_admin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisteredPlayer (val fullName: String, val rating: Int): Parcelable{
    var score = 0f
    var tpn: Int? = null
    var isActive: Boolean = true
}