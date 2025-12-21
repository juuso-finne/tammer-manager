package com.example.tammer_manager.data.tournament_admin.classes

import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.data.tournament_admin.pairing.topScorerOrOpponentColorImbalance
import com.example.tammer_manager.data.tournament_admin.pairing.topScorersOrOpponentsColorstreak
import com.example.tammer_manager.utils.generatePlayers
import com.example.tammer_manager.utils.simulateMatch
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.component4

class PairingAssessmentCriteriaTest {

    lateinit var playerA: RegisteredPlayer
    lateinit var playerB: RegisteredPlayer
    lateinit var playerC: RegisteredPlayer
    lateinit var playerD: RegisteredPlayer

    @Before
    fun setup(){
        val (a, b, c ,d) = generatePlayers(4)
        playerA = a
        playerB = b
        playerC = c
        playerD = d
    }

    @Test
    fun `Assessments are ordered correctly`(){
        assertThat(
            PairingAssessmentCriteria(pabAssigneeUnplayedGames = 1)
        )
        .isGreaterThan(
            PairingAssessmentCriteria(
            topScorerOrOpponentColorImbalanceCount = 2,
            topScorersOrOpponentsColorstreakCount = 1
            )
        )

        assertThat(
            PairingAssessmentCriteria(
                colorpreferenceConflicts = 3,
                strongColorpreferenceConflicts = 2
            )
        )
        .isGreaterThan(
            PairingAssessmentCriteria(
                colorpreferenceConflicts = 3,
                strongColorpreferenceConflicts = 1
            )
        )
    }

    @Test
    fun `Topscorers or opponents with color difference absolute value of more than 2 is counted correctly`(){
        val playerE = playerA.copy(score = 2f)
        val playerF = playerB.copy(score = 2f)

        val defaultColorPreferenceMap = mutableMapOf(
            Pair(playerE.id, ColorPreference(
                strength = ColorPreferenceStrength.ABSOLUTE,
                colorBalance = 2,
                preferredColor = PlayerColor.BLACK
            )),
            Pair(playerF.id, ColorPreference(
                strength = ColorPreferenceStrength.STRONG,
                colorBalance = 1,
                preferredColor = PlayerColor.BLACK
            ))
        )

        val defaultCandidate = Pair(playerE, playerF)

        fun testDriver(
            candidate: Pair<RegisteredPlayer, RegisteredPlayer> = defaultCandidate,
            roundsCompleted: Int = 2,
            maxRounds: Int? = null,
            colorPreferenceMap: Map<Int, ColorPreference> = defaultColorPreferenceMap
        ): Boolean{
            return topScorerOrOpponentColorImbalance(
                candidate = candidate,
                roundsCompleted = roundsCompleted,
                colorPreferenceMap = colorPreferenceMap,
                maxRounds = maxRounds ?: (roundsCompleted + 1)
            )
        }

        assertThat(testDriver()).isFalse()

        defaultColorPreferenceMap[playerF.id] = defaultColorPreferenceMap[playerF.id]?.copy(
            colorBalance = 3,
            strength = ColorPreferenceStrength.ABSOLUTE
        ) ?: ColorPreference()

        assertThat(testDriver()).isTrue()
        assertThat(testDriver(maxRounds = 4)).isFalse()
        assertThat(testDriver(roundsCompleted = 4)).isFalse()
    }

    @Test
    fun `topscorers or topscorers' opponents who get the same colour three times in a row is counted correctly`(){
        val playerE = playerA.copy()
        val playerF = playerB.copy()
        val playerG = playerC.copy()

        simulateMatch(playerE, playerG, 1f, 0f,1)
        simulateMatch(playerE, playerG, 1f, 0f,2)

        simulateMatch(playerF, playerG, 0f, 1f,1)
        simulateMatch(playerF, playerG, 0f, 1f,2)

        val defaultColorPreferenceMap = mutableMapOf<Int, ColorPreference>()
        listOf(playerE, playerF).forEach { defaultColorPreferenceMap[it.id] = it.getColorPreference() }

        val defaultCandidate = Pair(playerE, playerF)

        fun testDriver(
            candidate: Pair<RegisteredPlayer, RegisteredPlayer> = defaultCandidate,
            roundsCompleted: Int = 2,
            colorPreferenceMap: Map<Int, ColorPreference> = defaultColorPreferenceMap,
            maxRounds: Int? = null,
        ): Boolean{
            return topScorersOrOpponentsColorstreak(
                candidate = candidate,
                roundsCompleted = roundsCompleted,
                colorPreferenceMap = colorPreferenceMap,
                maxRounds = maxRounds ?: (roundsCompleted + 1)
            )
        }

        assertThat(testDriver()).isTrue()
        assertThat(testDriver(maxRounds = 4)).isFalse()
        assertThat(testDriver(roundsCompleted = 4)).isFalse()

        simulateMatch(playerG, playerF, 1f, 0f,3)
        defaultColorPreferenceMap[playerF.id] = playerF.getColorPreference()

        assertThat(testDriver()).isFalse()
    }
}