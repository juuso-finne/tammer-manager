package com.example.tammer_manager.data.tournament_admin.pairing.swiss

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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

data class ColorCount(
    var white: Int = 0,
    var black: Int = 0,

    var strongWhite: Int = 0,
    var strongBlack: Int= 0,

    var neutral: Int = 0
){
    fun update(preference: ColorPreference){
        if(preference.strength == ColorPreferenceStrength.NONE){
            neutral++
            return
        }

        if(preference.preferredColor == PlayerColor.WHITE){
            white++
            if(preference.strength >= ColorPreferenceStrength.STRONG){
                strongWhite++
            }
            return
        }

        black++
        if(preference.strength >= ColorPreferenceStrength.STRONG){
            strongBlack++
        }
    }
}

fun bestPossibleScore(
    players: List<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    maxPairs: Int = players.size/2
): PairingAssessmentCriteria{

    val omittedPairs = (players.size / 2) - maxPairs

    val count = ColorCount()

    for (i in 0 until players.size) {
        val it = players[i]
        val preference = colorPreferenceMap[it.id] ?: ColorPreference()
        count.update(preference)
    }

    val potentialColorConflicts = ((abs(count.white - count.black) - count.neutral) / 2) - omittedPairs
    val colorConflicts = max(potentialColorConflicts, 0)

    val potentialStrongConflicts = max(count.strongWhite, count.strongBlack) - (players.size / 2) -  omittedPairs - (players.size % 2)
    val strongConflicts = max(potentialStrongConflicts, 0)

    return PairingAssessmentCriteria(
        colorpreferenceConflicts = colorConflicts,
        strongColorpreferenceConflicts = strongConflicts
    )
}

fun bestPossibleSplitScore(
    s1: List<RegisteredPlayer>,
    s2: List<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>
): PairingAssessmentCriteria{

    val s1Count = ColorCount()
    val s2Count = ColorCount()

    for (i in s1.indices){
        val s1Preference = colorPreferenceMap[s1[i].id] ?: ColorPreference()
        val s2Preference = colorPreferenceMap[s2[i].id] ?: ColorPreference()

        s1Count.update(s1Preference)
        s2Count.update(s2Preference)
    }

    if(s2.size > s1.size){
        for (i in s1.size until s2.size){
            val preference = colorPreferenceMap[s2[i].id] ?: ColorPreference()
            s2Count.update(preference)
        }
    }

    var colorConflicts = s1.size
    colorConflicts -= min(s1Count.white, s2Count.black)
    colorConflicts -= min(s1Count.black, s2Count.white)
    colorConflicts -= s1Count.neutral
    colorConflicts -= min(colorConflicts, s2Count.neutral)

    val potentialStrongConflictsS1 = max(
        s1Count.strongBlack - s2Count.white - s2Count.neutral - (s2Count.black - s2Count.strongBlack),
        s1Count.strongWhite - s2Count.black - s2Count.neutral - (s2Count.white - s2Count.strongWhite)
    )
    val potentialStrongConflictsS2 = max(
        s2Count.strongBlack - s1Count.white - s1Count.neutral - (s2.size - s1.size) - (s1Count.black - s1Count.strongBlack),
        s2Count.strongWhite - s1Count.black - s1Count.neutral - (s2.size - s1.size) - (s1Count.white - s1Count.strongWhite)
    )

    val strongConflicts = max(max(potentialStrongConflictsS1, potentialStrongConflictsS2), 0)

    return PairingAssessmentCriteria(
        colorpreferenceConflicts = colorConflicts,
        strongColorpreferenceConflicts = strongConflicts
    )
}