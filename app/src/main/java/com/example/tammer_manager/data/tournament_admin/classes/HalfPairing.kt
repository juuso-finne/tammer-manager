package com.example.tammer_manager.data.tournament_admin.classes

import android.os.Parcelable
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import kotlinx.parcelize.Parcelize

@Parcelize
class HalfPairing(
    val playerID: Int? = null,
    val points: Float = 0f
): Parcelable

typealias Pairing = Map<PlayerColor, HalfPairing>
typealias PairingList = List<Pairing>