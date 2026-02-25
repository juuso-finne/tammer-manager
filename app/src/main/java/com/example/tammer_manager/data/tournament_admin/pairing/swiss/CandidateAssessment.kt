package com.example.tammer_manager.data.tournament_admin.pairing.swiss

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer

fun firstIneligiblePair(
    pairs: List<Pair<RegisteredPlayer, RegisteredPlayer>>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int
):Int?{
    pairs.forEachIndexed{ index, pair ->
        if (!passesAbsoluteCriteria(
                candidate = pair,
                roundsCompleted = roundsCompleted,
                colorPreferenceMap = colorPreferenceMap,
                isFinalRound = maxRounds - roundsCompleted <= 1
        )){
            return index
        }
    }
    return null
}

fun lastImperfectPair(
    pairs: List<Pair<RegisteredPlayer, RegisteredPlayer>>,
    bestScore: PairingAssessmentCriteria,
    baseScore: PairingAssessmentCriteria = PairingAssessmentCriteria(),
    cumulativeScore: PairingAssessmentCriteria,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int
):Int?{
    var output: Int? = null
    for(i in pairs.indices){
        val score  = assessPairing(
            candidate = pairs[i],
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            colorPreferenceMap = colorPreferenceMap
        )

        if (score > PairingAssessmentCriteria()){
            output = i
        }

        cumulativeScore += score

        if(cumulativeScore + baseScore >= bestScore){
            return i
        }
    }
    return output
}