package com.example.tammer_manager.data.tournament_admin.classes

import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.utils.generatePlayers
import com.example.tammer_manager.utils.simulateMatch
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class RegisteredPlayerTest {
    lateinit var playerA: RegisteredPlayer
    lateinit var playerB: RegisteredPlayer
    lateinit var playerC: RegisteredPlayer
    lateinit var playerD: RegisteredPlayer

    @Before
    fun setUp() {
        val (a, b, c ,d) = generatePlayers(4)
        playerA = a
        playerB = b
        playerC = c
        playerD = d

        simulateMatch(playerA, playerD, 1f, 0f, 1)
        simulateMatch(playerD, playerA, 0f, 1f, 2)
        simulateMatch(playerA, playerD, 1f, 0f, 3)
        simulateMatch(playerA, null, 1f, 0f, 4)
        simulateMatch(playerA, playerD, 1f, 0f, 5)

        simulateMatch(playerD, playerB, 0f, 1f, 1)
        simulateMatch(playerB, playerD, 1f, 0f, 2)
        simulateMatch(playerB, playerD, 1f, 0f, 3)
        simulateMatch(playerD, playerB, 0f, 1f, 4)

        simulateMatch(playerD, playerC, 0f, 1f, 1)
        simulateMatch(playerC, playerD, 1f, 0f, 2)
        simulateMatch(playerD, playerC, 0f, 1f, 3)
    }

    @Test
    fun `Color balance returns correct value`(){
        assertThat(playerA.getColorBalance()).isEqualTo(2)
        assertThat(playerB.getColorBalance()).isEqualTo(0)
        assertThat(playerC.getColorBalance()).isEqualTo(-1)
    }

    @Test
    fun `Same color in last n matches returns correct value`(){
        assertThat(playerA.sameColorInLastNRounds(2)).isTrue()
        assertThat(playerA.sameColorInLastNRounds(3)).isFalse()
        assertThat(playerB.sameColorInLastNRounds(2)).isFalse()
    }

    @Test
    fun `Color preference returns correct value`(){

        assertThat(playerA.getColorPreference()).isEqualTo(ColorPreference(
            preferredColor = PlayerColor.BLACK,
            colorBalance = 2,
            strength = ColorPreferenceStrength.ABSOLUTE
        ))

        assertThat(playerB.getColorPreference()).isEqualTo(ColorPreference(
            preferredColor = PlayerColor.WHITE,
            colorBalance = 0,
            strength = ColorPreferenceStrength.MILD
        ))

        assertThat(playerC.getColorPreference()).isEqualTo(ColorPreference(
            preferredColor = PlayerColor.WHITE,
            colorBalance = -1,
            strength = ColorPreferenceStrength.STRONG
        ))

        assertThat(playerA.copy(matchHistory = listOf()).getColorPreference()).isEqualTo(
            ColorPreference())
    }

}