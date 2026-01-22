package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.utils.generatePlayers
import com.example.tammer_manager.utils.simulateMatch
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BestPossibleScoreTest {
    @Test
    fun `Minimum number of conflicting color preferences is counted correctly`() {
        val players = generatePlayers(5)
        val (
            playerA,
            playerB,
            playerC,
            playerD,
            playerE
        ) = players

        val playerF = RegisteredPlayer("PlayerF", 0, 0 ,0)

        simulateMatch(playerA, playerF, 0f, 0f, 1)
        simulateMatch(playerA, playerF, 0f, 0f, 1)

        simulateMatch(playerB, playerF, 0f, 0f, 1)

        simulateMatch(playerF, playerC, 0f, 0f, 1)
        simulateMatch(playerC, playerF, 0f, 0f, 2)

        simulateMatch(playerF, playerD, 0f, 0f, 1)
        simulateMatch(playerD, playerF, 0f, 0f, 2)

        var colorPreferenceMap = players.associateBy(
            {it.id},
            {it.getColorPreference()}
        )

        assertThat(bestPossibleScore(players, colorPreferenceMap)).isEqualTo(
            PairingAssessmentCriteria(
                colorpreferenceConflicts = 1,
                strongColorpreferenceConflicts = 0
            )
        )

        simulateMatch(playerE, playerF, 0f, 0f, 1)

        colorPreferenceMap = players.associateBy(
            {it.id},
            {it.getColorPreference()}
        )

        assertThat(bestPossibleScore(players, colorPreferenceMap)).isEqualTo(
            PairingAssessmentCriteria(
                colorpreferenceConflicts = 2,
                strongColorpreferenceConflicts = 1
            )
        )
    }

}