package com.example.tammer_manager.data.tournament_admin.pairing.swiss

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import kotlin.math.abs
import kotlin.math.max

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
    for(i in pairs.indices.reversed()){
        val score  = assessPairing(
            candidate = pairs[i],
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            colorPreferenceMap = colorPreferenceMap
        )

        if (output == null && score > PairingAssessmentCriteria()){
            output = i
        }

        cumulativeScore += score

        if(cumulativeScore + baseScore >= bestScore){
            return output
        }
    }
    return output
}