package com.example.tammer_manager.data.tournament_admin.classes

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PairingAssessmentCriteriaTest {

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
    }
}