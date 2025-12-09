package com.example.tammer_manager.data.tournament_admin.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tournament (var name: String, val maxRounds: Int): Parcelable {
    var roundsCompleted = 0
}