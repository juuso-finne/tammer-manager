package com.example.tammer_manager.data.tournament_admin.pairing.swiss

import com.example.tammer_manager.data.combinatorics.IndexSwaps
import com.example.tammer_manager.data.combinatorics.applyIndexSwap
import com.example.tammer_manager.data.combinatorics.nextPermutation
import com.example.tammer_manager.data.tournament_admin.classes.CandidateAssessmentScore
import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
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
    remainderPairingScore: CandidateAssessmentScore,
    mdpPairingScore: CandidateAssessmentScore,
    combinedScore: CandidateAssessmentScore,
    lookForBestScore: Boolean,
    downfloats : MutableList<RegisteredPlayer> = mutableListOf(),
    bestBracketScore: PairingAssessmentCriteria,
    bestRemainderScore: PairingAssessmentCriteria,
    approvedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
    disapprovedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
){
    for(next in IndexSwaps(sizeS1 = s1.size, sizeS2 = s2.size).iterator()) {

        val swappingIndices = Pair(next.first.copyOf(), next.second.copyOf())
        remainderPairingScore.resetCurrentScore()

        applyIndexSwap(s1, s2, swappingIndices)
        iterateS2Permutations(
            remainingPlayers = remainingPlayers,
            s1 = s1,
            s2 = s2.sorted().toMutableList(),
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            maxPairs = maxPairs,
            limbo = limbo,
            remainderPairingScore = remainderPairingScore,
            combinedScore = combinedScore,
            lookForBestScore = lookForBestScore,
            downfloats = downfloats,
            mdpPairingScore = mdpPairingScore,
            bestRemainderScore = bestRemainderScore,
            approvedDownfloaters = approvedDownfloaters,
            disapprovedDownfloaters = disapprovedDownfloaters
        )
        if (!combinedScore.isValidCandidate) {
            continue
        }

        if (!lookForBestScore || combinedScore.bestTotal <= bestBracketScore) {
            return
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
    remainderPairingScore: CandidateAssessmentScore,
    mdpPairingScore: CandidateAssessmentScore,
    combinedScore: CandidateAssessmentScore,
    downfloats : MutableList<RegisteredPlayer> = mutableListOf(),
    bestRemainderScore: PairingAssessmentCriteria,
    approvedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
    disapprovedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>>,
){
    val changedIndices = mutableListOf<Int>()
    val isLastBracket = remainingPlayers.isEmpty()
    val byeInBracket = isLastBracket && (s2.size + s1.size) % 2 == 1

    do{
        if (byeInBracket && s2.last().receivedPairingBye){
            remainderPairingScore.resetCurrentScore()
            continue
        }

        assessCandidate(
            s1 = s1,
            s2 = s2,
            changedIndices = changedIndices,
            score = remainderPairingScore,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds
        )

        if(byeInBracket){
            remainderPairingScore.currentTotal.pabAssigneeUnplayedGames = roundsCompleted - s2.last().matchHistory.size
            remainderPairingScore.currentTotal.pabAssigneeScore = s2.last().score
        }

        if(!remainderPairingScore.isValidCandidate){
            continue
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

        combinedScore.resetCurrentScore()
        combinedScore.currentTotal += remainderPairingScore.currentTotal
        combinedScore.currentTotal += mdpPairingScore.currentTotal
        combinedScore.isValidCandidate = true

        if (combinedScore.updateHiScore(remainderPairingScore.currentCandidate.plus(mdpPairingScore.currentCandidate))){
            downfloats.clear()
            downfloats.addAll(s2.subList(maxPairs, s2.size))

            if(byeInBracket){
                combinedScore.bestCandidate.add(Pair(s2.last(), null))
            }
        }

        if(!lookForBestScore || remainderPairingScore.currentTotal <= bestRemainderScore){
            return
        }

    }while(nextPermutation(list = s2, changedIndices = changedIndices, length = maxPairs))
}