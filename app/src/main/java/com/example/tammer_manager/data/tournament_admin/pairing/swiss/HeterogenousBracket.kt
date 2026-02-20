package com.example.tammer_manager.data.tournament_admin.pairing.swiss

import com.example.tammer_manager.data.combinatorics.IndexSwaps
import com.example.tammer_manager.data.combinatorics.applyIndexSwap
import com.example.tammer_manager.data.combinatorics.nextPermutation
import com.example.tammer_manager.data.combinatorics.setupPermutationSkip
import com.example.tammer_manager.data.tournament_admin.classes.CandidateAssessmentScore
import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import kotlin.collections.iterator
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
    incomingDownfloaters:List<RegisteredPlayer>,
    approvedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
    disapprovedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
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

    val mdpPairingScore = PairingAssessmentCriteria()
    val combinedScore = CandidateAssessmentScore()

    for(next in IndexSwaps(sizeS1 = s1.size, sizeS2 = s2.size).iterator()){

        mdpPairingScore.reset()

        val swappingIndices = Pair(next.first.copyOf(), next.second.copyOf())

        applyIndexSwap(s1, s2, swappingIndices)

        val s2Copy = s2.sorted().toMutableList()
        iterateMdpOpponents(
            limbo = limbo,
            remainingPlayers = remainingPlayers,
            s2Downfloats = s2Downfloats,
            s1 = s1,
            s2 = s2Copy,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            mdpPairingScore = mdpPairingScore,
            combinedScore = combinedScore,
            lookForBestScore = lookForBestScore,
            maxPairs = maxPairs,
            approvedDownfloaters = approvedDownfloaters,
            disapprovedDownfloaters = disapprovedDownfloaters
        )

        if(combinedScore.isValidCandidate && !lookForBestScore){
            return true
        }

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
        incomingDownfloaters = limbo.plus(s2Downfloats),
        approvedDownfloaters = approvedDownfloaters,
        disapprovedDownfloaters = disapprovedDownfloaters
    )
}

fun iterateMdpOpponents(
    remainingPlayers: MutableList<RegisteredPlayer>,
    limbo: MutableList<RegisteredPlayer>,
    s2Downfloats: MutableList<RegisteredPlayer>,
    s1: List<RegisteredPlayer>,
    s2: MutableList<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    mdpPairingScore: PairingAssessmentCriteria,
    combinedScore: CandidateAssessmentScore,
    lookForBestScore: Boolean,
    maxPairs: Int,
    approvedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
    disapprovedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
){
    val remainderPairingScore = PairingAssessmentCriteria()

    do{
        val candidate = s1.mapIndexed { index, it ->
            Pair(it, s2[index])
        }

        val firstIneligiblePair = firstIneligiblePair(
            pairs = candidate,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds
        )

        // Skip to next iteration if candidate isn't viable
        firstIneligiblePair?.let{
            setupPermutationSkip(
                list = s2,
                i = it,
                length = maxPairs
            )
            continue
        }

        var lastImperfectPair:Int? = null

        if(lookForBestScore){
            lastImperfectPair = lastImperfectPair(
                pairs = candidate,
                bestScore = combinedScore.bestTotal,
                cumulativeScore = remainderPairingScore,
                colorPreferenceMap = colorPreferenceMap,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds
            )
        }

        val remainder = s2.subList(s1.size, s2.size)
        val remainderPairs = min(remainder.size/2, maxPairs)

        val s1R = remainder.subList(0, remainderPairs)
        val s2R = remainder.subList(remainderPairs, remainder.size)

        iterateHomogenousBracket(
            remainingPlayers = remainingPlayers,
            s1 = s1R,
            s2 = s2R,
            limbo = limbo,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            maxPairs = remainderPairs,
            remainderPairingScore = remainderPairingScore,
            mdpPairingScore = mdpPairingScore,
            mdpPairs = candidate,
            combinedScore = combinedScore,
            lookForBestScore = lookForBestScore,
            downfloats = s2Downfloats,
            approvedDownfloaters = approvedDownfloaters,
            disapprovedDownfloaters = disapprovedDownfloaters
        )

        if(!combinedScore.isValidCandidate){
            continue
        }

        if(!lookForBestScore || lastImperfectPair == null){
            return
        }

        if (combinedScore.bestTotal == PairingAssessmentCriteria()){
            return
        }

        setupPermutationSkip(
            list = s2,
            i = lastImperfectPair,
            length = maxPairs
        )

    }while(nextPermutation(list = s2, length = s1.size))
    return
}