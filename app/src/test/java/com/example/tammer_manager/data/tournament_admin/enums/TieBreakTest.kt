package com.example.tammer_manager.data.tournament_admin.enums

import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.utils.generatePlayers
import com.example.tammer_manager.utils.simulateMatch
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.component4

class TieBreakTest {
    lateinit var players: List<RegisteredPlayer>

    lateinit var playerA: RegisteredPlayer
    lateinit var playerB: RegisteredPlayer
    lateinit var playerC: RegisteredPlayer
    lateinit var playerD: RegisteredPlayer
    lateinit var playerE: RegisteredPlayer

    lateinit var playerF: RegisteredPlayer
    lateinit var playerG: RegisteredPlayer

    @Before
    fun setUp() {
        players = generatePlayers(8)
        val (a, b, c, d) = players.subList(0,4)
        playerA = a
        playerB = b
        playerC = c
        playerD = d

        val (e,f,g) = players.subList(4, 7)
        playerE = e
        playerF = f
        playerG = g



        simulateMatch(playerA, playerE, 1f, 0f, 1)
        simulateMatch(playerF, playerB, 0f, 1f, 1)
        simulateMatch(playerC, playerD, 1f, 0f, 1)
        simulateMatch(playerG, null, 1f, 0f, 1)

        simulateMatch(playerB, playerA, .5f, .5f, 2)
        simulateMatch(playerG, playerC, 1f, 0f, 2)
        simulateMatch(playerD, playerF, 1f, 0f, 2)
        simulateMatch(playerE, null, 1f, 0f, 2)

        simulateMatch(playerA, playerG, 0f, 1f, 3)
        simulateMatch(playerC, playerB, 1f, 0f, 3)
        simulateMatch(playerE, playerD, 0f, 1f, 3)
        simulateMatch(playerF, null, 1f, 0f, 3)

        simulateMatch(playerG, playerD, 1f, 0f, 4)
        simulateMatch(playerB, playerE, 0f, 1f, 4)
        simulateMatch(playerF, playerC, 1f, 0f, 4)
        simulateMatch(playerA, null, 1f, 0f, 4)
    }

    @Test
    fun `Wins returns correct values`(){
        assertThat(TieBreak.WINS.calculate(playerA, players)).isEqualTo(1f)
        assertThat(TieBreak.WINS.calculate(playerB, players)).isEqualTo(1f)
        assertThat(TieBreak.WINS.calculate(playerC, players)).isEqualTo(2f)
        assertThat(TieBreak.WINS.calculate(playerD, players)).isEqualTo(2f)
        assertThat(TieBreak.WINS.calculate(playerE, players)).isEqualTo(1f)
        assertThat(TieBreak.WINS.calculate(playerF, players)).isEqualTo(1f)
        assertThat(TieBreak.WINS.calculate(playerG, players)).isEqualTo(4f)
    }

    @Test
    fun `Median-Buchholz returns correct values`(){
        assertThat(TieBreak.MEDIAN_BUCHHOLZ.calculate(playerA, players)).isEqualTo(2f)
        assertThat(TieBreak.MEDIAN_BUCHHOLZ.calculate(playerB, players)).isEqualTo(4f)
        assertThat(TieBreak.MEDIAN_BUCHHOLZ.calculate(playerC, players)).isEqualTo(4f)
        assertThat(TieBreak.MEDIAN_BUCHHOLZ.calculate(playerD, players)).isEqualTo(4f)
        assertThat(TieBreak.MEDIAN_BUCHHOLZ.calculate(playerE, players)).isEqualTo(2f)
        assertThat(TieBreak.MEDIAN_BUCHHOLZ.calculate(playerF, players)).isEqualTo(2f)
        assertThat(TieBreak.MEDIAN_BUCHHOLZ.calculate(playerG, players)).isEqualTo(2f)
    }

    @Test
    fun `Buchholz returns correct values`(){
        assertThat(TieBreak.BUCHHOLZ.calculate(playerA, players)).isEqualTo(7.5f)
        assertThat(TieBreak.BUCHHOLZ.calculate(playerB, players)).isEqualTo(8.5f)
        assertThat(TieBreak.BUCHHOLZ.calculate(playerC, players)).isEqualTo(9.5f)
        assertThat(TieBreak.BUCHHOLZ.calculate(playerD, players)).isEqualTo(10f)
        assertThat(TieBreak.BUCHHOLZ.calculate(playerE, players)).isEqualTo(6.5f)
        assertThat(TieBreak.BUCHHOLZ.calculate(playerF, players)).isEqualTo(5.5f)
        assertThat(TieBreak.BUCHHOLZ.calculate(playerG, players)).isEqualTo(6.5f)
    }

    @Test fun `Sonneborn-Berger returns correct values`(){
        assertThat(TieBreak.SONNEBORN_BERGER.calculate(playerA, players)).isEqualTo(2.75f)
        assertThat(TieBreak.SONNEBORN_BERGER.calculate(playerB, players)).isEqualTo(3.25f)
        assertThat(TieBreak.SONNEBORN_BERGER.calculate(playerC, players)).isEqualTo(3.5f)
        assertThat(TieBreak.SONNEBORN_BERGER.calculate(playerD, players)).isEqualTo(4f)
        assertThat(TieBreak.SONNEBORN_BERGER.calculate(playerE, players)).isEqualTo(3.5f)
        assertThat(TieBreak.SONNEBORN_BERGER.calculate(playerF, players)).isEqualTo(2f)
        assertThat(TieBreak.SONNEBORN_BERGER.calculate(playerG, players)).isEqualTo(6.5f)
    }

}