package com.example.tammer_manager.data.tournament_admin.classes

import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.data.tournament_admin.pairing.colorpreferenceConflict
import com.example.tammer_manager.data.tournament_admin.pairing.strongColorpreferenceConflict
import com.example.tammer_manager.data.tournament_admin.pairing.topScorerOrOpponentColorImbalance
import com.example.tammer_manager.data.tournament_admin.pairing.topScorersOrOpponentsColorStreak
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
    fun `Topscorers or opponents with color difference absolute value of more than 2 are recognized`(){
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
    fun `topscorers or topscorers' opponents who get the same colour three times in a row are recognized`(){
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
            return topScorersOrOpponentsColorStreak(
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

    @Test
    fun `Color preference conflicts are recognized`(){

        fun testDriver(
            candidate: Pair<RegisteredPlayer, RegisteredPlayer> = Pair(playerA, playerB),
            colorPreferenceMap: Map<Int, ColorPreference>,
        ): Boolean{
            return colorpreferenceConflict(
                candidate = candidate,
                colorPreferenceMap = colorPreferenceMap,
            )
        }

        val colorPreferenceMap = mutableMapOf<Int, ColorPreference>()

        colorPreferenceMap[playerA.id] = ColorPreference(
            strength = ColorPreferenceStrength.MILD,
            colorBalance = -1,
            preferredColor = PlayerColor.WHITE
        )

        colorPreferenceMap[playerB.id] = ColorPreference(
            strength = ColorPreferenceStrength.MILD,
            colorBalance = 1,
            preferredColor = PlayerColor.BLACK
        )

        assertThat(testDriver(colorPreferenceMap = colorPreferenceMap)).isFalse()

        colorPreferenceMap[playerB.id] = ColorPreference(
            strength = ColorPreferenceStrength.MILD,
            colorBalance = -1,
            preferredColor = PlayerColor.WHITE
        )

        assertThat(testDriver(colorPreferenceMap = colorPreferenceMap)).isTrue()

        colorPreferenceMap[playerB.id] = colorPreferenceMap[playerB.id]!!.copy(strength = ColorPreferenceStrength.NONE)

        assertThat(testDriver(colorPreferenceMap = colorPreferenceMap)).isFalse()
    }

    @Test
    fun `Strong color preference conflicts are recognized`(){
        fun testDriver(
            candidate: Pair<RegisteredPlayer, RegisteredPlayer> = Pair(playerA, playerB),
            colorPreferenceMap: Map<Int, ColorPreference>,
        ): Boolean{
            return strongColorpreferenceConflict(
                candidate = candidate,
                colorPreferenceMap = colorPreferenceMap,
            )
        }

        val colorPreferenceMap = mutableMapOf<Int, ColorPreference>()

        colorPreferenceMap[playerA.id] = ColorPreference(
            strength = ColorPreferenceStrength.STRONG,
            colorBalance = -1,
            preferredColor = PlayerColor.WHITE
        )

        colorPreferenceMap[playerB.id] = ColorPreference(
            strength = ColorPreferenceStrength.MILD,
            colorBalance = -1,
            preferredColor = PlayerColor.WHITE
        )

        assertThat(testDriver(colorPreferenceMap = colorPreferenceMap)).isFalse()

        colorPreferenceMap[playerB.id] = ColorPreference(
            strength = ColorPreferenceStrength.STRONG,
            colorBalance = -1,
            preferredColor = PlayerColor.WHITE
        )

        assertThat(testDriver(colorPreferenceMap = colorPreferenceMap)).isTrue()

        colorPreferenceMap[playerB.id] = colorPreferenceMap[playerB.id]!!.copy(strength = ColorPreferenceStrength.NONE)

        assertThat(testDriver(colorPreferenceMap = colorPreferenceMap)).isFalse()
    }

    @Test
    fun `Plus operator returns expected result`(){
        val a = PairingAssessmentCriteria(
            pabAssigneeUnplayedGames = 1,
            topScorerOrOpponentColorImbalanceCount = 2,
            topScorersOrOpponentsColorstreakCount = 3,
            colorpreferenceConflicts = 4,
            strongColorpreferenceConflicts = 5
        )

        val b = PairingAssessmentCriteria(
            pabAssigneeUnplayedGames = 2,
            topScorerOrOpponentColorImbalanceCount = 2,
            topScorersOrOpponentsColorstreakCount = 2,
            colorpreferenceConflicts = 2,
            strongColorpreferenceConflicts = 2
        )
        a += b

        assertThat(a).isEqualTo(
            PairingAssessmentCriteria(
                pabAssigneeUnplayedGames = 3,
                topScorerOrOpponentColorImbalanceCount = 4,
                topScorersOrOpponentsColorstreakCount = 5,
                colorpreferenceConflicts = 6,
                strongColorpreferenceConflicts = 7
            )
        )
    }
    @Test
    fun `Minus operator returns expected result`(){
        val a = PairingAssessmentCriteria(
            pabAssigneeUnplayedGames = 9,
            topScorerOrOpponentColorImbalanceCount = 8,
            topScorersOrOpponentsColorstreakCount = 7,
            colorpreferenceConflicts = 6,
            strongColorpreferenceConflicts = 5
        )

        val b = PairingAssessmentCriteria(
            pabAssigneeUnplayedGames = 2,
            topScorerOrOpponentColorImbalanceCount = 2,
            topScorersOrOpponentsColorstreakCount = 2,
            colorpreferenceConflicts = 2,
            strongColorpreferenceConflicts = 2
        )

        a -= b
        assertThat(a).isEqualTo(
            PairingAssessmentCriteria(
                pabAssigneeUnplayedGames = 7,
                topScorerOrOpponentColorImbalanceCount = 6,
                topScorersOrOpponentsColorstreakCount = 5,
                colorpreferenceConflicts = 4,
                strongColorpreferenceConflicts = 3
            )
        )
    }

    @Test
    fun `Reset methods resets the object`(){
        val a = PairingAssessmentCriteria(
            pabAssigneeUnplayedGames = 9,
            topScorerOrOpponentColorImbalanceCount = 8,
            topScorersOrOpponentsColorstreakCount = 7,
            colorpreferenceConflicts = 6,
            strongColorpreferenceConflicts = 5
        )

        a.reset()

        assertThat(a).isEqualTo(PairingAssessmentCriteria())
    }
}