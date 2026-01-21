package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.combinatorics.IndexSwaps
import com.example.tammer_manager.data.combinatorics.applyIndexSwap
import com.example.tammer_manager.data.combinatorics.nextPermutation
import com.example.tammer_manager.data.tournament_admin.classes.CandidateAssessmentScore
import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import kotlin.math.min

fun pairHeterogenousBracket(
    output: MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>>,
    remainingPlayers: MutableList<RegisteredPlayer>,
    residentPlayers: List<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    maxPairs: Int,
    lookForBestScore: Boolean,
    incomingDownfloaters:List<RegisteredPlayer>
):Boolean{
    val isLastBracket = remainingPlayers.isEmpty()

    if(isLastBracket && maxPairs < (incomingDownfloaters.size + residentPlayers.size) / 2){
        return false
    }

    val mdpsToPair = min(incomingDownfloaters.size, maxPairs)

    val s1 = incomingDownfloaters.take(mdpsToPair).toMutableList()
    val s2 = residentPlayers.toMutableList()

    val limbo = incomingDownfloaters.takeLast(incomingDownfloaters.size - mdpsToPair).toMutableList()
    val s2Downfloats = mutableListOf<RegisteredPlayer>()

    val mdpPairingScore = CandidateAssessmentScore()
    val remainderPairingScore = CandidateAssessmentScore()
    val combinedScore = CandidateAssessmentScore()

    for(next in IndexSwaps(sizeS1 = s1.size, sizeS2 = s2.size).iterator()){

        mdpPairingScore.resetAll()

        val swappingIndices = Pair(next.first.copyOf(), next.second.copyOf())

        applyIndexSwap(s1, s2, swappingIndices)

        val s2Copy = s2.sorted().toMutableList()
        iterateMdps(
            limbo = limbo,
            remainingPlayers = remainingPlayers,
            s2Downfloats = s2Downfloats,
            s1 = s1,
            s2 = s2Copy,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            maxPairs = mdpsToPair,
            mdpPairingScore = mdpPairingScore,
            combinedScore = combinedScore,
            lookForBestScore = lookForBestScore,
            remainderPairingScore = remainderPairingScore
        )

        applyIndexSwap(s1, s2, swappingIndices)
    }

    if (!combinedScore.isValidCandidate){
        return false
    }

    output.addAll(combinedScore.bestCandidate)

    if (isLastBracket){
        return combinedScore.isValidCandidate
    }

    return nextBracket(
        output = output,
        remainingPlayers = remainingPlayers,
        colorPreferenceMap = colorPreferenceMap,
        roundsCompleted = roundsCompleted,
        maxRounds = maxRounds,
        lookForBestScore = true,
        incomingDownfloaters = limbo.plus(s2Downfloats)
    )
}

fun iterateMdps(
    remainingPlayers: MutableList<RegisteredPlayer>,
    limbo: MutableList<RegisteredPlayer>,
    s2Downfloats: MutableList<RegisteredPlayer>,
    s1: List<RegisteredPlayer>,
    s2: MutableList<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    maxPairs: Int,
    mdpPairingScore: CandidateAssessmentScore,
    remainderPairingScore: CandidateAssessmentScore,
    combinedScore: CandidateAssessmentScore,
    lookForBestScore: Boolean,
){
    val changedIndices = mutableListOf<Int>()

    do{
        assessCandidate(
            s1 = s1,
            s2 = s2,
            changedIndices = changedIndices,
            score = mdpPairingScore,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds
        )

        if(!mdpPairingScore.isValidCandidate){
            continue
        }

        if(lookForBestScore && mdpPairingScore.currentTotal >= combinedScore.bestTotal){
            continue
        }

        combinedScore.currentTotal.reset()
        combinedScore.currentTotal += mdpPairingScore.currentTotal.copy()

        val remainder = s2.subList(s1.size, s2.size)
        val remainderPairs = (maxPairs - s1.size)

        val s1R = remainder.subList(0, remainderPairs)
        val s2R = remainder.subList(remainderPairs, remainder.size)

        remainderPairingScore.resetAll()

        iterateHomogenousBracket(
            remainingPlayers = remainingPlayers,
            s1 = s1R,
            s2 = s2R,
            limbo = limbo,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            maxPairs = remainderPairs,
            score = remainderPairingScore,
            combinedScore = combinedScore,
            lookForBestScore = lookForBestScore,
            downfloats = s2Downfloats
        )

        if(!combinedScore.isValidCandidate){
            continue
        }

        if(!lookForBestScore){
            return
        }

        if(combinedScore.currentTotal < combinedScore.bestTotal){
            combinedScore.updateHiScore(mutableListOf(), mutableListOf())
            combinedScore.bestCandidate.addAll(remainderPairingScore.bestCandidate)
            combinedScore.bestCandidate.addAll(mdpPairingScore.bestCandidate)
        }

        if (combinedScore.bestTotal == PairingAssessmentCriteria()){
            return
        }

    }while(nextPermutation(list = s2, changedIndices = changedIndices, length = maxPairs))
    return
}