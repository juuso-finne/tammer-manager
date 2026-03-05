package com.example.tammer_manager.data.tournament_admin.enums

import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.utils.generatePlayers
import com.example.tammer_manager.utils.simulateMatch
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.component4

class TieBreakTest {
    lateinit var playerA: RegisteredPlayer
    lateinit var playerB: RegisteredPlayer
    lateinit var playerC: RegisteredPlayer
    lateinit var playerD: RegisteredPlayer
    lateinit var playerE: RegisteredPlayer
    @Before
    fun setUp() {
        val players = generatePlayers(5)
        val (a, b, c, d, e) = players
        playerA = a
        playerB = b
        playerC = c
        playerD = d
        playerE = e

        simulateMatch(playerA, playerC, 1f, 0f, 1)
        simulateMatch(playerD, playerB, 0f, 1f, 1)
        simulateMatch(playerE, null, 1f, 0f, 1)

        simulateMatch(playerB, playerA, .5f, .5f, 2)
        simulateMatch(playerC, playerE, 1f, 0f, 2)
        simulateMatch(playerD, null, 1f, 0f, 2)

        simulateMatch(playerE, playerB, 0f, 1f, 3)
        simulateMatch(playerA, playerD, 0f, 2f, 3)
        simulateMatch(playerC, null, 1f, 0f, 3)
    }

}