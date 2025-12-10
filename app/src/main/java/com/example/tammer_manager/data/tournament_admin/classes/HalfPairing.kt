package com.example.tammer_manager.data.tournament_admin.classes

import android.os.Parcelable
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class HalfPairing(
    val playerID: Int? = null,
    var points: Float? = null
): Parcelable

typealias Pairing = Map<PlayerColor, HalfPairing>
typealias PairingList = List<Pairing>