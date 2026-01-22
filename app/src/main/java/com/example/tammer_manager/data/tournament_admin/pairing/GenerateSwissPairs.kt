package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.classes.PairingList
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import kotlin.math.abs

fun pairingsPlaceholder(players: List<RegisteredPlayer>):PairingList{
    val output = mutableListOf<Pairing>()
    val sortedPlayerList = players.sortedByDescending { it.score }

    for(i in 0..< sortedPlayerList.size - 1 step 2){
        val playerA = sortedPlayerList[i]
        val playerB = sortedPlayerList[i+1]

        val pairing = mapOf<PlayerColor, HalfPairing>(
    Pair(PlayerColor.WHITE, HalfPairing(playerID = playerA.id)),
            Pair(PlayerColor.BLACK, HalfPairing(playerID = playerB.id))
        )

        output.add(pairing)
    }
    return output
}

fun generateSwissPairs(players: List<RegisteredPlayer>): PairingList{
    return pairingsPlaceholder(players)
}

fun assignColors(players: Pair<RegisteredPlayer, RegisteredPlayer?>, colorPreferenceMap: Map<Int, ColorPreference>):Pairing{
    val (playerA, playerB) = players
    val preferenceA = colorPreferenceMap[playerA.id]
    val preferenceB = colorPreferenceMap[playerB?.id]

    if(preferenceB == null){
        return mapOf(
            Pair(PlayerColor.WHITE, HalfPairing(playerID = playerA.id)),
                    Pair(PlayerColor.BLACK, HalfPairing())
        )
    }

    if(preferenceA?.strength == ColorPreferenceStrength.NONE && preferenceB.strength == ColorPreferenceStrength.NONE){
        val playerAColor = PlayerColor.entries.random()

        return mapOf(
            Pair(playerAColor, HalfPairing(playerID = playerA.id)),
            Pair(playerAColor.reverse(), HalfPairing(playerID = playerB?.id))
        )
    }

    if(preferenceA?.preferredColor != preferenceB.preferredColor){
        return mapOf(
            Pair(preferenceA!!.preferredColor!!, HalfPairing(playerID = playerA.id)),
            Pair(preferenceB.preferredColor!!, HalfPairing(playerID = playerB?.id))
        )
    }

    val preferenceOrder = players.toList().sortedWith(
        compareByDescending<RegisteredPlayer?>{ colorPreferenceMap[it?.id]?.strength }
            .thenByDescending { abs(colorPreferenceMap[it?.id]?.colorBalance ?: 0) }
            .thenByDescending { it?.score }
            .thenBy { it?.tpn }
    )

    val preferredPlayerId = preferenceOrder.first()?.id
    val preferredPlayerColor = colorPreferenceMap[preferredPlayerId]?.preferredColor

    return mapOf(
        Pair(preferredPlayerColor!!, HalfPairing(playerID = preferredPlayerId)),
            Pair(preferredPlayerColor.reverse(), HalfPairing(playerID = preferenceOrder.last()?.id))
    )
}