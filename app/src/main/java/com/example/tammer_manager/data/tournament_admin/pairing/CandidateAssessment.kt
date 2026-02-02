package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.CandidateAssessmentScore
import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max

fun assessCandidate(
    s1: List<RegisteredPlayer>,
    s2: List<RegisteredPlayer>,
    changedIndices: MutableList<Int>,
    score: CandidateAssessmentScore,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int
){
    if (score.currentIndividualAssessments.isEmpty()){
        for (i in 0 until min(s1.size, s2.size)){
            val pair = Pair(s1[i], s2[i])
            if (!passesAbsoluteCriteria(
                candidate = pair,
                roundsCompleted = roundsCompleted,
                colorPreferenceMap = colorPreferenceMap,
                isFinalRound = maxRounds - roundsCompleted <= 1
            )){
                score.resetCurrentScore()
                return
            }

            val assessment = assessPairing(
                candidate = pair,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds,
                colorPreferenceMap = colorPreferenceMap
            )

            score.currentIndividualAssessments.add(assessment)
            score.currentCandidate.add(pair)
            score.currentTotal += assessment
        }
        score.isValidCandidate = true
        return
    }

    for (i in changedIndices){
        val pair = Pair(s1[i], s2[i])
        if (!passesAbsoluteCriteria(
                candidate = pair,
                roundsCompleted = roundsCompleted,
                colorPreferenceMap = colorPreferenceMap,
                isFinalRound = maxRounds - roundsCompleted <= 1
            )){
            score.resetCurrentScore()
            return
        }

        val newAssessment = assessPairing(
            candidate = pair,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            colorPreferenceMap = colorPreferenceMap
        )

        val oldAssessment = score.currentIndividualAssessments[i]
        score.currentTotal -= oldAssessment
        score.currentTotal += newAssessment

        score.currentIndividualAssessments[i] = newAssessment
        score.currentCandidate[i] = pair
    }

    score.isValidCandidate = true
    return
}

fun bestPossibleScore(
    players: List<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    maxPairs: Int = players.size/2
): PairingAssessmentCriteria{

    val omittedPairs = (players.size / 2) - maxPairs

    var white = 0
    var black = 0

    var strongWhite = 0
    var strongBlack = 0

    var neutral = 0

    for (i in 0 until players.size) {
        val it = players[i]
        val preference = colorPreferenceMap[it.id] ?: ColorPreference()

        if(preference.strength == ColorPreferenceStrength.NONE){
            neutral++
            continue
        }

        if(preference.preferredColor == PlayerColor.WHITE){
            white++
            if(preference.strength >= ColorPreferenceStrength.STRONG){
                strongWhite++
            }
            continue
        }

        black++
        if(preference.strength >= ColorPreferenceStrength.STRONG){
            strongBlack++
        }
    }

    val potentialColorConflicts = ((abs(white - black) - neutral) / 2) - omittedPairs
    val colorConflicts = max(potentialColorConflicts, 0)

    val potentialStrongConflicts = max(strongWhite, strongBlack) - (players.size / 2) -  omittedPairs - (players.size % 2)
    val strongConflicts = max(potentialStrongConflicts, 0)

    return PairingAssessmentCriteria(
        colorpreferenceConflicts = colorConflicts,
        strongColorpreferenceConflicts = strongConflicts
    )
}