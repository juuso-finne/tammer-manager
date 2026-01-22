package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.combinatorics.IndexSwaps
import com.example.tammer_manager.data.combinatorics.applyIndexSwap
import com.example.tammer_manager.data.combinatorics.nextPermutation
import com.example.tammer_manager.data.tournament_admin.classes.CandidateAssessmentScore
import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer

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
    bestBracketScore: PairingAssessmentCriteria
){
    val bestRemainderScore = bestPossibleScore(s1.plus(s2), colorPreferenceMap)
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
            bestRemainderScore = bestRemainderScore
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
    bestRemainderScore: PairingAssessmentCriteria
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
        }

        if(!remainderPairingScore.isValidCandidate){
            continue
        }

        val compatibleWithLowerBrackets = (
            isLastBracket ||
            nextBracket(
                output = mutableListOf(),
                remainingPlayers = remainingPlayers.toMutableList(),
                colorPreferenceMap = colorPreferenceMap,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds,
                lookForBestScore = false,
                incomingDownfloaters = s2.subList(maxPairs, s2.size).plus(limbo).sorted()
            )
        )

        if(!compatibleWithLowerBrackets){
            continue
        }

        combinedScore.resetCurrentScore()
        combinedScore.currentTotal += remainderPairingScore.currentTotal
        combinedScore.currentTotal += mdpPairingScore.currentTotal
        combinedScore.isValidCandidate = true

        if (combinedScore.updateHiScore(remainderPairingScore.currentCandidate.plus(mdpPairingScore.currentCandidate))){
            downfloats.clear()
            downfloats.addAll(limbo)
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