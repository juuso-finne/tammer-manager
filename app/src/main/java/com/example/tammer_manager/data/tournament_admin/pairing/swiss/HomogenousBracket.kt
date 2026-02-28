package com.example.tammer_manager.data.tournament_admin.pairing.swiss

import com.example.tammer_manager.data.combinatorics.IndexSwaps
import com.example.tammer_manager.data.combinatorics.applyIndexSwap
import com.example.tammer_manager.data.combinatorics.nextPermutation
import com.example.tammer_manager.data.combinatorics.setupPermutationSkip
import com.example.tammer_manager.data.tournament_admin.classes.BracketScoringData
import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import kotlin.collections.iterator

fun iterateHomogenousBracket(
    remainingPlayers: MutableList<RegisteredPlayer>,
    s1: MutableList<RegisteredPlayer>,
    s2: MutableList<RegisteredPlayer>,
    limbo: MutableList<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    maxPairs: Int,
    bracketData: BracketScoringData,
    lookForBestScore: Boolean,
    downfloats : MutableList<RegisteredPlayer> = mutableListOf(),
    approvedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
    disapprovedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
){
    for(next in IndexSwaps(sizeS1 = s1.size, sizeS2 = s2.size).iterator()) {

        val swappingIndices = Pair(next.first.copyOf(), next.second.copyOf())

        applyIndexSwap(s1, s2, swappingIndices)

        bracketData.remainderSplitTheoreticalBest = bestPossibleSplitScore(s1, s2, colorPreferenceMap)
        val bestPotential = bracketData.remainderSplitTheoreticalBest + bracketData.mdpPairingScore

        if(
            !bracketData.foundValidCandidate ||
            bestPotential.compareByColorConflict(bracketData.bestTotal) < 0
        ){
            iterateS2Permutations(
                remainingPlayers = remainingPlayers,
                s1 = s1,
                s2 = s2.sorted().toMutableList(),
                colorPreferenceMap = colorPreferenceMap,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds,
                maxPairs = maxPairs,
                limbo = limbo,
                bracketData = bracketData,
                lookForBestScore = lookForBestScore,
                downfloats = downfloats,
                approvedDownfloaters = approvedDownfloaters,
                disapprovedDownfloaters = disapprovedDownfloaters
            )
        }

        if (bracketData.foundValidCandidate) {
            if(
                !lookForBestScore ||
                bracketData.foundBestPossibleScore ||
                (bracketData.mdpPairingScore + bracketData.remainderTheoreticalBest).compareByColorConflict(bracketData.bestTotal) >= 0
            ){
                return
            }
        }
        applyIndexSwap(s1, s2, swappingIndices)
    }
}

fun iterateS2Permutations(
    remainingPlayers: MutableList<RegisteredPlayer>,
    s1: List<RegisteredPlayer>,
    s2: MutableList<RegisteredPlayer>,
    limbo: MutableList<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    maxPairs: Int,
    lookForBestScore: Boolean,
    bracketData: BracketScoringData,
    downfloats : MutableList<RegisteredPlayer> = mutableListOf(),
    approvedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
    disapprovedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
){
    val isLastBracket = remainingPlayers.isEmpty()
    val byeInBracket = isLastBracket && (s2.size + s1.size) % 2 == 1

    do{
        if (byeInBracket && s2.last().receivedPairingBye){
            continue
        }

        bracketData.setRemainderPairs(s1, s2)
        bracketData.remainderPairingScore.reset()

        val firstIneligiblePair = firstIneligiblePair(
            pairs = bracketData.remainderPairs,
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

        if(byeInBracket){
            bracketData.remainderPairingScore.pabAssigneeUnplayedGames = roundsCompleted - s2.last().matchHistory.size
            bracketData.remainderPairingScore.pabAssigneeScore = s2.last().score
        }

        val candidateDownfloaters = s2.subList(maxPairs, s2.size).plus(limbo).sorted()

        val compatibleWithLowerBrackets =
            if(isLastBracket)
                true
            else
                nextBracket(
                    output = mutableListOf(),
                    remainingPlayers = remainingPlayers.toMutableList(),
                    colorPreferenceMap = colorPreferenceMap,
                    roundsCompleted = roundsCompleted,
                    maxRounds = maxRounds,
                    lookForBestScore = false,
                    incomingDownfloaters = candidateDownfloaters,
                    approvedDownfloaters = approvedDownfloaters,
                    disapprovedDownfloaters = disapprovedDownfloaters
                )


        if(!compatibleWithLowerBrackets){
            disapprovedDownfloaters[remainingPlayers.first().score]?.add(candidateDownfloaters.toSet())
            continue
        }

        if(!isLastBracket){
            approvedDownfloaters[remainingPlayers.first().score]?.add(candidateDownfloaters.toSet())
        }

        var lastImperfectPair: Int? = null

        if(lookForBestScore){
            lastImperfectPair  = lastImperfectPair(
                pairs = bracketData.remainderPairs,
                bestScore = bracketData.bestTotal,
                baseScore = bracketData.mdpPairingScore,
                cumulativeScore = bracketData.remainderPairingScore,
                colorPreferenceMap = colorPreferenceMap,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds,
                theoreticalBest = bracketData.remainderSplitTheoreticalBest,
                foundValidCandidate = bracketData.foundValidCandidate
            )
        }

        bracketData.foundValidCandidate = true

        if(!lookForBestScore){
            return
        }

        if (bracketData.updateHiScore()){
            downfloats.clear()
            downfloats.addAll(s2.subList(maxPairs, s2.size))

            if(byeInBracket){
                bracketData.bestCandidate.add(Pair(s2.last(), null))
            }
        }

        if(
            bracketData.foundBestPossibleScore ||
            bracketData.remainderPairingScore.compareByColorConflict(bracketData.remainderSplitTheoreticalBest) <= 0
        ){
            return
        }

        setupPermutationSkip(
            list = s2,
            i = lastImperfectPair ?: s2.indices.last,
            length = maxPairs
        )

    }while(nextPermutation(list = s2, length = maxPairs))
}