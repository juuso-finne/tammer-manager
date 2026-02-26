package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.data.tournament_admin.pairing.swiss.bestPossibleScore
import com.example.tammer_manager.data.tournament_admin.pairing.swiss.bestPossibleSplitScore
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

        val playerX = RegisteredPlayer("PlayerX", 0, 0 ,0)

        simulateMatch(playerA, playerX, 0f, 0f, 1)
        simulateMatch(playerA, playerX, 0f, 0f, 1)

        simulateMatch(playerB, playerX, 0f, 0f, 1)

        simulateMatch(playerX, playerC, 0f, 0f, 1)
        simulateMatch(playerC, playerX, 0f, 0f, 2)

        simulateMatch(playerX, playerD, 0f, 0f, 1)
        simulateMatch(playerD, playerX, 0f, 0f, 2)

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

        simulateMatch(playerE, playerX, 0f, 0f, 1)

        colorPreferenceMap = players.associateBy(
            {it.id},
            {it.getColorPreference()}
        )

        assertThat(bestPossibleScore(players, colorPreferenceMap)).isEqualTo(
            PairingAssessmentCriteria(
                colorpreferenceConflicts = 2,
                strongColorpreferenceConflicts = 0
            )
        )

        assertThat(
            bestPossibleScore(
                players,
                colorPreferenceMap,
                maxPairs = 1
            )
        ).isEqualTo(PairingAssessmentCriteria(
            colorpreferenceConflicts = 1,
            strongColorpreferenceConflicts = 0
        ))
    }

    @Test
    fun `BestPossibleSplitScore gives correct assessment of split groups minimum conflicts`(){
        val players = generatePlayers(11)
        val (
            playerA,
            playerB,
            playerC,
            playerD,
            playerE
        ) = players.subList(0,5)
        val(
            playerF,
            playerG,
            playerH,
            playerI,
            playerJ
        ) = players.subList(5,10)

        val playerK = players[10]
        val playerX = RegisteredPlayer("PlayerX", 0, 0 ,0)

        // mild white
        simulateMatch(playerA, playerX, 0f, 0f,0)
        simulateMatch(playerX, playerA, 0f, 0f,1)

        // strong white
        simulateMatch(playerX, playerB, 0f, 0f,0)

        // strong black
        simulateMatch(playerC, playerX, 0f, 0f,0)

        // strong black
        simulateMatch(playerD, playerX, 0f, 0f,0)

        // strong black
        simulateMatch(playerE, playerX, 0f, 0f,0)

        // strong black
        simulateMatch(playerF, playerX, 0f, 0f,0)

        //strong black
        simulateMatch(playerG, playerX, 0f, 0f,0)

        //strong white
        simulateMatch(playerX, playerH, 0f, 0f,0)

        //absolute black
        simulateMatch(playerI, playerX, 0f, 0f,0)
        simulateMatch(playerI, playerX, 0f, 0f,1)

        // mild white
        simulateMatch(playerJ, playerX, 0f, 0f,0)
        simulateMatch(playerX, playerJ, 0f, 0f,1)

        // strong black
        simulateMatch(playerK, playerX, 0f, 0f, 0)

        var colorPreferenceMap = players.associateBy(
            {it.id},
            {it.getColorPreference()}
        )

        assertThat(
            bestPossibleSplitScore(
                players.subList(0,5),
                players.subList(5, 11),
                colorPreferenceMap
            )
        ).isEqualTo(
            PairingAssessmentCriteria(
                colorpreferenceConflicts = 1,
                strongColorpreferenceConflicts = 1
            )
        )

        simulateMatch(playerB, playerX, 0f, 0f,1)
        colorPreferenceMap = players.associateBy(
            {it.id},
            {it.getColorPreference()}
        )

        assertThat(
            bestPossibleSplitScore(
                players.subList(0,5),
                players.subList(5, 11),
                colorPreferenceMap
            )
        ).isEqualTo(
            PairingAssessmentCriteria(
                colorpreferenceConflicts = 2,
                strongColorpreferenceConflicts = 1
            )
        )
    }

}