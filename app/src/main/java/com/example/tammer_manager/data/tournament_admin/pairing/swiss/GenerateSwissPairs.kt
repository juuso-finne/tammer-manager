package com.example.tammer_manager.data.tournament_admin.pairing.swiss

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.data.tournament_admin.pairing.fast_swiss.fastNextBracket
import kotlin.collections.get
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun generateSwissPairs(
    output: MutableList<Pairing>,
    players: List<RegisteredPlayer>,
    roundsCompleted: Int,
    maxRounds: Int,
): Boolean{
    val playerPairs: MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>> = mutableListOf()

    val colorPreferenceMap = players.associateBy(
        {it.id},
        {it.getColorPreference()}
    )

    if (nextBracket(
        output = playerPairs,
        remainingPlayers = players.sorted().toMutableList(),
        colorPreferenceMap = colorPreferenceMap,
        roundsCompleted = roundsCompleted,
        maxRounds = maxRounds
    )
    ){
        playerPairs.sortWith(
            compareByDescending<Pair<RegisteredPlayer, RegisteredPlayer?>> { max(it.first.score, (it.second?.score ?: 0f)) }
                .thenByDescending { it.first.score + (it.second?.score ?: 0f)}
                .thenBy { min(it.first.tpn, (it.second?.tpn ?: 0)) }
        )
        playerPairs.mapTo(output){assignColors(it, colorPreferenceMap)}
        return true
    }

    return false
}

fun assignColors(players: Pair<RegisteredPlayer, RegisteredPlayer?>, colorPreferenceMap: Map<Int, ColorPreference>):Pairing{
    val (playerA, playerB) = players
    val preferenceA = colorPreferenceMap[playerA.id]
    val preferenceB = colorPreferenceMap[playerB?.id]

    if(preferenceB == null){
        return mapOf(
            Pair(PlayerColor.WHITE, HalfPairing(playerID = playerA.id, points = 1f)),
                    Pair(PlayerColor.BLACK, HalfPairing(points = 0f))
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
        val playerAColor = preferenceA?.preferredColor ?: preferenceB.preferredColor!!.reverse()
        val playerBColor = preferenceB.preferredColor ?: preferenceA?.preferredColor!!.reverse()

        return mapOf(
            Pair(playerAColor, HalfPairing(playerID = playerA.id)),
            Pair(playerBColor, HalfPairing(playerID = playerB?.id))
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