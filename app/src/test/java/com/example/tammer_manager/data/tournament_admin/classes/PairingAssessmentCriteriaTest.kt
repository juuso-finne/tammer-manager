package com.example.tammer_manager.data.tournament_admin.classes

import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.pairing.topScorerOrOpponentColorImbalance
import com.example.tammer_manager.utils.generatePlayers
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

        val candidate = Pair(playerE, playerF)
        val colorPreferenceMap = mutableMapOf(
            Pair(playerE.id, ColorPreference(
                strength = ColorPreferenceStrength.ABSOLUTE,
                colorBalance = 2
            )),
            Pair(playerF.id, ColorPreference(
                strength = ColorPreferenceStrength.STRONG,
                colorBalance = 1
            ))
        )

        fun testDriver(
            candidate: Pair<RegisteredPlayer, RegisteredPlayer> = Pair(playerE, playerF),
            roundsCompleted: Int = 2,
            colorPreferenceMap: Map<Int, ColorPreference> = mapOf()
        ): Boolean{
            return topScorerOrOpponentColorImbalance(
                candidate = candidate,
                roundsCompleted = roundsCompleted,
                colorPreferenceMap = colorPreferenceMap
            )
        }

        assertThat(testDriver(colorPreferenceMap = colorPreferenceMap)).isFalse()

        colorPreferenceMap[playerE.id] = colorPreferenceMap[playerE.id]?.copy(colorBalance = 3) ?: ColorPreference()

        assertThat(testDriver(colorPreferenceMap = colorPreferenceMap)).isTrue()
        assertThat(testDriver(colorPreferenceMap = colorPreferenceMap, roundsCompleted = 4)).isFalse()
    }
}