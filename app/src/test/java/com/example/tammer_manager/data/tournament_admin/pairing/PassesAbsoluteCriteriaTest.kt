package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.MatchHistoryItem
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.utils.generatePlayers
import com.example.tammer_manager.utils.simulateMatch
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PassesAbsoluteCriteriaTest {

    @Test
    fun `Two participants shall not play against each other more than once`() {
        val (playerA, playerB) = generatePlayers(2)
        simulateMatch(playerA, playerB, 0f, 1f, 1)

        assertThat(passesAbsoluteCriteria(
            candidate =  listOf(Pair(playerA, playerB)),
            roundsCompleted =  1,
            colorPreferenceMap = mapOf()
        )).isFalse()
    }

    @Test
    fun `Non-topscorers with the same absolute colour preference shall not meet`(){
        val (playerA, playerB, playerC) = generatePlayers(3)
        simulateMatch(playerA, playerC, 0.5f, 1f, 1)
        simulateMatch(playerA, playerC, 0.5f, 1f, 2)
        simulateMatch(playerB, playerC, 0.5f, 1f, 1)
        simulateMatch(playerB, playerC, 0.5f, 1f, 2)

        val colorPreferences = mapOf<Int, ColorPreference>(
            Pair(playerA.id, playerA.getColorPreference()),
            Pair(playerB.id, playerB.getColorPreference())
        )

        assertThat(passesAbsoluteCriteria(
            candidate =  listOf(Pair(playerA, playerB)),
            roundsCompleted =  3,
            colorPreferenceMap = colorPreferences,
            isFinalRound = true
        )).isFalse()
    }

    @Test
    fun `Topscorers with the same absolute colour preference can play`(){
        val (playerA, playerB, playerC) = generatePlayers(3)
        simulateMatch(playerA, playerC, 1f, 0f, 1)
        simulateMatch(playerA, playerC, 1f, 0f, 2)
        simulateMatch(playerB, playerC, 1f, 0f, 1)
        simulateMatch(playerB, playerC, 1f, 0f, 2)

        val colorPreferences = mapOf<Int, ColorPreference>(
            Pair(playerA.id, playerA.getColorPreference()),
            Pair(playerB.id, playerB.getColorPreference())
        )

        assertThat(playerA.getColorPreference()).isEqualTo(
            ColorPreference(
                colorBalance = 2,
                preferredColor = PlayerColor.BLACK,
                strength = ColorPreferenceStrength.ABSOLUTE
            )
        )

        assertThat(playerB.getColorPreference()).isEqualTo(
            ColorPreference(
                colorBalance = 2,
                preferredColor = PlayerColor.BLACK,
                strength = ColorPreferenceStrength.ABSOLUTE
            )
        )

        assertThat(passesAbsoluteCriteria(
            candidate =  listOf(Pair(playerA, playerB)),
            roundsCompleted =  3,
            colorPreferenceMap = colorPreferences,
            isFinalRound = true
        )).isTrue()
    }

    @Test
    fun `Players who have not played before, and don't have absolute color preference, should be accepted`(){
        val (playerA, playerB, playerC) = generatePlayers(3)
        simulateMatch(playerA, playerC, 1f, 0f, 1)
        simulateMatch(playerB, playerC, 1f, 0f, 1)

        assertThat(passesAbsoluteCriteria(
            candidate =  listOf(Pair(playerA, playerB)),
            roundsCompleted =  1,
            colorPreferenceMap = mapOf()
        )).isTrue()
    }
}