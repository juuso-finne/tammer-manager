package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.utils.generatePlayers
import com.example.tammer_manager.utils.simulateMatch
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class NextBracketTest {

    lateinit var playerA: RegisteredPlayer
    lateinit var playerB: RegisteredPlayer
    lateinit var playerC: RegisteredPlayer
    lateinit var playerD: RegisteredPlayer
    lateinit var playerE: RegisteredPlayer
    lateinit var playerF: RegisteredPlayer

    lateinit var players: MutableList<RegisteredPlayer>

    lateinit var colorPreferenceMap: Map<Int, ColorPreference>

    @Before
    fun setup(){
        players = generatePlayers(5).toMutableList()
        val (a,b,c,d,e) = players

        playerA = a
        playerB = b
        playerC = c
        playerD = d
        playerE = e

        playerF = playerE.copy(id = 6, tpn = 6)
        players.add(playerF)

        simulateMatch(playerA, playerB, 1f, 0f, 1)
        simulateMatch(playerC, playerD, 1f, 0f, 1)
        simulateMatch(playerE, playerF, 1f, 0f, 1)

        simulateMatch(playerA, playerD, 1f, 0f, 2)
        simulateMatch(playerB, playerE, 1f, 0f, 2)
        simulateMatch(playerC, playerF, 1f, 0f, 2)

        colorPreferenceMap = players.associateBy(
            {it.id},
            {it.getColorPreference()}
        )
    }

    @Test
    fun `nextBracket outputs the best pairings`(){
        val pairings = mutableListOf<Pair<RegisteredPlayer, RegisteredPlayer?>>()

        nextBracket(
            output = pairings,
            remainingPlayers = players.toMutableList(),
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = 2,
            maxRounds = 4,
            incomingDownfloaters = listOf()
        )

        pairings.forEachIndexed { i, it ->
            println("Pair ${i + 1}:")
            println(it.first)
            println(it.second)
            println("-----")
        }
    }

}