package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.utils.generatePlayers
import com.example.tammer_manager.utils.simulateMatch
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.math.max
import kotlin.math.min

class NextBracketTest {

    lateinit var playerA: RegisteredPlayer
    lateinit var playerB: RegisteredPlayer
    lateinit var playerC: RegisteredPlayer
    lateinit var playerD: RegisteredPlayer
    lateinit var playerE: RegisteredPlayer
    lateinit var playerF: RegisteredPlayer
    lateinit var playerG: RegisteredPlayer
    lateinit var playerH: RegisteredPlayer
    lateinit var playerI: RegisteredPlayer
    lateinit var playerJ: RegisteredPlayer

    lateinit var players: MutableList<RegisteredPlayer>

    lateinit var colorPreferenceMap: Map<Int, ColorPreference>

    @Before
    fun setup(){
        players = generatePlayers(10).toMutableList()

        players.sort()
        
        val (a,b,c,d,e) = players

        playerA = a
        playerB = b
        playerC = c
        playerD = d
        playerE = e


        val (f,g,h,i,j) = players.subList(5, 10)

        playerF = f
        playerG = g
        playerH = h
        playerI = i
        playerJ = j

        players.removeAt(4)

        simulateMatch(playerF, playerA, 0f, 1f, 1)
        simulateMatch(playerG, playerB, 1f, 0f, 1)
        simulateMatch(playerC, playerH, .5f, .5f, 1)
        simulateMatch(playerI, playerD, 0f, 1f, 1)
        simulateMatch(playerJ, null, 1f, 0f, 1)
        playerJ.receivedPairingBye = true

        simulateMatch(playerA, playerG, 0f, 1f, 2)
        simulateMatch(playerD, playerJ, 1f, 0f, 2)
        simulateMatch(playerB, playerC, 1f, 0f, 2)
        simulateMatch(playerH, playerF, 1f, 0f, 2)
        simulateMatch(playerI, null, 1f, 1f, 2)
        playerI.receivedPairingBye = true

        colorPreferenceMap = players.associateBy(
            {it.id},
            {it.getColorPreference()}
        )
    }

    @Test
    fun `nextBracket outputs the best pairings`(){
        val pairings = mutableListOf<Pair<RegisteredPlayer, RegisteredPlayer?>>()

        val success = nextBracket(
            output = pairings,
            remainingPlayers = players.sorted().toMutableList(),
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = 2,
            maxRounds = 4,
            incomingDownfloaters = listOf()
        )

        pairings.sortWith(
            compareByDescending<Pair<RegisteredPlayer, RegisteredPlayer?>> { max(it.first.score, (it.second?.score ?: 0f)) }
                .thenByDescending { it.first.score + (it.second?.score ?: 0f)}
                .thenBy { min(it.first.tpn, (it.second?.tpn ?: 0)) }
        )

        pairings.forEachIndexed { i, it ->
            println("Pair ${i + 1}:")
            println(it.first)
            println(it.second)
            println("-----")
        }

        println("Success: $success")
    }

}