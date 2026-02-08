package com.example.tammer_manager.data.tournament_admin.pairing


import com.example.tammer_manager.data.tournament_admin.classes.CandidateAssessmentScore
import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.pairing.swiss.assessCandidate
import com.example.tammer_manager.utils.generatePlayers
import com.example.tammer_manager.utils.simulateMatch
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class AssessCandidateTest {

    lateinit var playerA: RegisteredPlayer
    lateinit var playerB: RegisteredPlayer
    lateinit var playerC: RegisteredPlayer
    lateinit var playerD: RegisteredPlayer
    lateinit var playerE: RegisteredPlayer
    lateinit var playerF: RegisteredPlayer

    lateinit var players: MutableList<RegisteredPlayer>

    lateinit var colorPreferenceMap: Map<Int, ColorPreference>

    @Before
    fun setup(){
        players = generatePlayers(5).toMutableList()
        val (a,b,c,d,e) = players

        playerA = a
        playerB = b
        playerC = c
        playerD = d
        playerE = e

        playerF = playerE.copy(id = 6, tpn = 6)
        players.add(playerF)

        simulateMatch(playerA, playerB, 1f, 0f, 1)
        simulateMatch(playerC, playerD, 1f, 0f, 1)
        simulateMatch(playerE, playerF, 1f, 0f, 1)

        simulateMatch(playerA, playerD, 1f, 0f, 2)
        simulateMatch(playerB, playerE, 1f, 0f, 2)
        simulateMatch(playerC, playerF, 1f, 0f, 2)

        colorPreferenceMap = players.associateBy(
            {it.id},
            {it.getColorPreference()}
        )
    }

    @Test
    fun `Invalid pairings (players played each other before) are identified`(){
        val s1 = mutableListOf(playerA, playerC, playerE)
        val s2 = mutableListOf(playerB, playerD, playerF)

        val score = CandidateAssessmentScore()

        assessCandidate(s1, s2, mutableListOf(), score, colorPreferenceMap, 2, 4)

        assertThat(score.isValidCandidate).isFalse()
    }

    @Test
    fun`Invalid pairings (conflicting absolute color preference) are identified`(){
        val s1 = mutableListOf(playerA, playerB, playerE)
        val s2 = mutableListOf(playerC, playerF, playerD)

        val score = CandidateAssessmentScore()

        assessCandidate(s1, s2, mutableListOf(), score, colorPreferenceMap, 2, 4)

        assertThat(score.isValidCandidate).isFalse()
    }

    @Test
    fun`Absolute color preference conflict allowed for topscorers`(){
        val s1 = mutableListOf(playerA, playerB, playerE)
        val s2 = mutableListOf(playerC, playerF, playerD)

        val score = CandidateAssessmentScore()

        assessCandidate(s1, s2, mutableListOf(), score, colorPreferenceMap, 2, 3)

        assertThat(score.isValidCandidate).isTrue()
    }

    @Test
    fun`Candidates are assessed correctly`(){
        val s1 = mutableListOf(playerA, playerB, playerE)
        val s2 = mutableListOf(playerC, playerF, playerD)

        val score = CandidateAssessmentScore()

        assessCandidate(s1, s2, mutableListOf(), score, colorPreferenceMap, 2, 3)

        assertThat(score.currentTotal).isEqualTo(
            PairingAssessmentCriteria(
                pabAssigneeUnplayedGames = 0,
                topScorerOrOpponentColorImbalanceCount = 1,
                topScorersOrOpponentsColorstreakCount = 1,
                colorpreferenceConflicts = 2,
                strongColorpreferenceConflicts = 1
            )
        )
        assertThat(score.isValidCandidate).isTrue()
    }

    @Test
    fun `Perfect candidate is recognized`(){
        val s1 = mutableListOf(playerA, playerB, playerE)
        val s2 = mutableListOf(playerF, playerD, playerC)

        val score = CandidateAssessmentScore()

        assessCandidate(s1, s2, mutableListOf(), score, colorPreferenceMap, 2, 3)

        assertThat(score.currentTotal).isEqualTo(PairingAssessmentCriteria())
        assertThat(score.isValidCandidate).isTrue()
    }

    @Test
    fun `New order is taken into account and assessed correctly`(){
        val s1 = mutableListOf(playerA, playerB, playerE)
        val s2 = mutableListOf(playerC, playerF, playerD)

        val score = CandidateAssessmentScore()

        assessCandidate(s1, s2, mutableListOf(), score, colorPreferenceMap, 2, 3)

        assertThat(score.currentTotal).isEqualTo(
            PairingAssessmentCriteria(
                pabAssigneeUnplayedGames = 0,
                topScorerOrOpponentColorImbalanceCount = 1,
                topScorersOrOpponentsColorstreakCount = 1,
                colorpreferenceConflicts = 2,
                strongColorpreferenceConflicts = 1
            )
        )

        s2[0] = playerF
        s2[1] = playerD
        s2[2] = playerC

        assessCandidate(s1, s2, mutableListOf(0, 1, 2), score, colorPreferenceMap, 2, 3)

        assertThat(score.currentTotal).isEqualTo(PairingAssessmentCriteria())
        assertThat(score.isValidCandidate).isTrue()

        s2[0] = playerD
        s2[1] = playerF

        assessCandidate(s1, s2, mutableListOf(0, 1), score, colorPreferenceMap, 2, 3)

        assertThat(score.currentTotal).isEqualTo(PairingAssessmentCriteria())
        assertThat(score.isValidCandidate).isFalse()
    }
}