package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.CandidateAssessmentScore
import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import kotlin.math.min

fun assessCandidate(
    s1: List<RegisteredPlayer>,
    s2: List<RegisteredPlayer>,
    changedIndices: MutableList<Int>,
    score: CandidateAssessmentScore,
    roundsCompleted: Int,
    colorPreferenceMap: Map<Int, ColorPreference>,
    isFinalRound: Boolean = false
):Boolean{
    if (score.currentIndividualAssessments.isEmpty()){
        for (i in 0 until min(s1.size, s2.size)){
            val pair = Pair(s1[i], s2[i])
            if (!passesAbsoluteCriteria(
                candidate = pair,
                roundsCompleted = roundsCompleted,
                colorPreferenceMap = colorPreferenceMap
            )){
                return false
            }
        }
    }
    return true
}