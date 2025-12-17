package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.MatchHistoryItem
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PassesAbsoluteCriteriaTest {

    @Test
    fun `Two participants shall not play against each other more than once`() {
        val playerA = RegisteredPlayer(
            fullName = "X",
            rating = 1000,
            id = 1,
            tpn = 1,
            matchHistory = listOf(MatchHistoryItem(
                opponentId = 2,
                round = 1,
                result = 0f,
                color = PlayerColor.WHITE
            ))
        )

        val playerB = RegisteredPlayer(
            fullName = "Y",
            rating = 1000,
            id = 2,
            tpn = 2,
            matchHistory = listOf(MatchHistoryItem(
                opponentId = 1,
                round = 1,
                result = 0f,
                color = PlayerColor.WHITE
            ))
        )

        assertThat(passesAbsoluteCriteria(
            pairing =  listOf(Pair(playerA, playerB)),
            roundsCompleted =  1,
            colorPreferenceMap = mapOf()
        )).isFalse()
    }

    @Test
    fun `Non-topscorers with the same absolute colour preference shall not meet`(){
        val playerA = RegisteredPlayer(
            fullName = "X",
            rating = 1000,
            id = 1,
            tpn = 1,
            matchHistory = listOf(
                MatchHistoryItem(
                    opponentId = 3,
                    round = 1,
                    result = 0f,
                    color = PlayerColor.WHITE
                ),
                MatchHistoryItem(
                    opponentId = 4,
                    round = 2,
                    result = 0f,
                    color = PlayerColor.WHITE
                ),
            ),
            score = 2f
        )

        val playerB = RegisteredPlayer(
            fullName = "Y",
            rating = 1000,
            id = 2,
            tpn = 2,
            matchHistory = listOf(
                MatchHistoryItem(
                    opponentId = 3,
                    round = 1,
                    result = 0f,
                    color = PlayerColor.WHITE
                ),
                MatchHistoryItem(
                    opponentId = 4,
                    round = 2,
                    result = 0f,
                    color = PlayerColor.WHITE
                ),
            ),
            score = 2f
        )

        val colorPreferences = mapOf<Int, ColorPreference>(
            Pair(playerA.id, playerA.getColorPreference()),
            Pair(playerB.id, playerB.getColorPreference())
        )

        assertThat(passesAbsoluteCriteria(
            pairing =  listOf(Pair(playerA, playerB)),
            roundsCompleted =  3,
            colorPreferenceMap = colorPreferences,
            isFinalRound = true
        )).isFalse()
    }

    @Test
    fun `Players who have not played before, and it is not the last round, should be accepted`(){
        val playerA = RegisteredPlayer(
            fullName = "X",
            rating = 1000,
            id = 1,
            tpn = 1,
            matchHistory = listOf(MatchHistoryItem(
                opponentId = 7,
                round = 1,
                result = 0f,
                color = PlayerColor.WHITE
            ))
        )

        val playerB = RegisteredPlayer(
            fullName = "Y",
            rating = 1000,
            id = 2,
            tpn = 2,
            matchHistory = listOf(MatchHistoryItem(
                opponentId = 5,
                round = 1,
                result = 0f,
                color = PlayerColor.WHITE
            ))
        )

        assertThat(passesAbsoluteCriteria(
            pairing =  listOf(Pair(playerA, playerB)),
            roundsCompleted =  1,
            colorPreferenceMap = mapOf()
        )).isTrue()
    }
}