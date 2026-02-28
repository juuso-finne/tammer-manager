package com.example.tammer_manager.data.tournament_admin.pairing.swiss

import com.example.tammer_manager.data.combinatorics.IndexSwaps
import com.example.tammer_manager.data.combinatorics.applyIndexSwap
import com.example.tammer_manager.data.combinatorics.nextPermutation
import com.example.tammer_manager.data.combinatorics.setupPermutationSkip
import com.example.tammer_manager.data.tournament_admin.classes.BracketScoringData
import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
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

    val mdpsToPair = min(incomingDownfloaters.size, maxPairs)

    val s1 = incomingDownfloaters.take(mdpsToPair).toMutableList()
    val s2 = residentPlayers.toMutableList()

    val limbo = incomingDownfloaters.takeLast(incomingDownfloaters.size - mdpsToPair).toMutableList()

    if(isLastBracket && maxPairs < (incomingDownfloaters.size + residentPlayers.size) / 2){
        s2.addAll(0, limbo)
        limbo.clear()
    }

    val s2Downfloats = mutableListOf<RegisteredPlayer>()
    val bracketData = BracketScoringData()

    bracketData.bracketTheoreticalBest += bestPossibleScore(
        players = s1.plus(s2),
        colorPreferenceMap = colorPreferenceMap,
        maxPairs =
            if (isLastBracket) (s1.size + s2.size) / 2
            else maxPairs,
    )

    for(next in IndexSwaps(sizeS1 = s1.size, sizeS2 = s2.size).iterator()){

        val swappingIndices = Pair(next.first.copyOf(), next.second.copyOf())

        applyIndexSwap(s1, s2, swappingIndices)

        iterateMdpOpponents(
            limbo = limbo,
            remainingPlayers = remainingPlayers,
            s2Downfloats = s2Downfloats,
            s1 = s1,
            s2 = s2.sorted().toMutableList(),
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            bracketData = bracketData,
            lookForBestScore = lookForBestScore,
            maxPairs =
                if (isLastBracket) (s1.size + s2.size) / 2
                else maxPairs,
            approvedDownfloaters = approvedDownfloaters,
            disapprovedDownfloaters = disapprovedDownfloaters
        )

        if(bracketData.foundValidCandidate){
            if(!lookForBestScore){
                return true
            }

            if (bracketData.foundBestPossibleScore){
                break
            }
        }

        applyIndexSwap(s1, s2, swappingIndices)
    }

    if (!bracketData.foundValidCandidate){
        return false
    }

    output.addAll(bracketData.bestCandidate)

    if (isLastBracket){
        return bracketData.foundValidCandidate
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
    bracketData: BracketScoringData,
    lookForBestScore: Boolean,
    maxPairs: Int,
    approvedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
    disapprovedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
){
    do{
        bracketData.setMdpPairs(s1, s2)
        bracketData.mdpPairingScore.reset()

        val firstIneligiblePair = firstIneligiblePair(
            pairs = bracketData.mdpPairs,
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

        val remainder = s2.subList(s1.size, s2.size)
        val remainderPairs = min(remainder.size/2, maxPairs)

        val s1R = remainder.subList(0, remainderPairs)
        val s2R = remainder.subList(remainderPairs, remainder.size)

        if(lookForBestScore){
            lastImperfectPair = lastImperfectPair(
                pairs = bracketData.mdpPairs,
                bestScore = bracketData.bestTotal,
                cumulativeScore = bracketData.mdpPairingScore,
                colorPreferenceMap = colorPreferenceMap,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds
            )

            bracketData.remainderTheoreticalBest = bestPossibleScore(remainder, colorPreferenceMap, remainderPairs)

            val bestPotential = bracketData.mdpPairingScore + bracketData.remainderTheoreticalBest

            if(
                bestPotential.compareByColorConflict(bracketData.bestTotal) >= 0 ||
                (bracketData.foundValidCandidate && bestPotential.compareByColorConflict(bracketData.bracketTheoreticalBest) > 0)
            ){
                setupPermutationSkip(
                    list = s2,
                    i = lastImperfectPair ?: s2.indices.last,
                    length = maxPairs
                )
                continue
            }
        }


        iterateHomogenousBracket(
            remainingPlayers = remainingPlayers,
            s1 = s1R,
            s2 = s2R,
            limbo = limbo,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            maxPairs = remainderPairs,
            bracketData = bracketData,
            lookForBestScore = lookForBestScore,
            downfloats = s2Downfloats,
            approvedDownfloaters = approvedDownfloaters,
            disapprovedDownfloaters = disapprovedDownfloaters
        )

        if(!bracketData.foundValidCandidate){
            setupPermutationSkip(
                list = s2,
                i = lastImperfectPair ?: s2.indices.last,
                length = maxPairs
            )
            continue
        }

        if(
            !lookForBestScore ||
            bracketData.foundBestPossibleScore){
            return
        }

    }while(nextPermutation(list = s2, length = s1.size))
    return
}