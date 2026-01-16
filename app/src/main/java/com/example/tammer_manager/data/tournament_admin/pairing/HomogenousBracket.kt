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
    score: CandidateAssessmentScore,
    lookForBestScore: Boolean
): Boolean{
    if (s1.isEmpty()){
        score.bestTotal = PairingAssessmentCriteria()
        score.isValidCandidate = true
        return true
    }

    for(next in IndexSwaps(sizeS1 = s1.size, sizeS2 = s2.size).iterator()){
        val swappingIndices = Pair(next.first.copyOf(), next.second.copyOf())
        applyIndexSwap(s1, s2, swappingIndices)
        if (iterateS2(
            remainingPlayers = remainingPlayers,
            s1 = s1,
            s2 = s2.sorted().toMutableList(),
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            maxPairs = maxPairs,
            limbo = limbo,
            score = score,
            lookForBestScore = lookForBestScore,
            isRemainder = true
        )){
            return true
        }
        applyIndexSwap(s1, s2, swappingIndices)
    }
    return false
}

fun iterateS2(
    remainingPlayers: MutableList<RegisteredPlayer>,
    s1: List<RegisteredPlayer>,
    s2: MutableList<RegisteredPlayer>,
    limbo: MutableList<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    maxPairs: Int,
    score: CandidateAssessmentScore,
    lookForBestScore: Boolean,
    isRemainder: Boolean
): Boolean{
    val changedIndices = mutableListOf<Int>()
    val isLastBracket = remainingPlayers.isEmpty()
    val byeInBracket = isLastBracket && isRemainder && (s2.size + s1.size) % 2 == 1

    do{
        assessCandidate(
            s1 = s1,
            s2 = s2,
            changedIndices = changedIndices,
            score = score,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds
        )

        if(byeInBracket){
            if (s2.last().receivedPairingBye ){
                score.isValidCandidate = false
            }
            score.currentTotal.pabAssigneeUnplayedGames = roundsCompleted - s2.last().matchHistory.size
        }

        if(!score.isValidCandidate){
            continue
        }

        if(lookForBestScore && score.currentTotal >= score.bestTotal){
            continue
        }

        if(isRemainder){
            val compatibleWithLowerBrackets = (
                isLastBracket ||
                nextBracket(
                    output = mutableListOf(),
                    remainingPlayers = remainingPlayers.toMutableList(),
                    colorPreferenceMap = colorPreferenceMap,
                    roundsCompleted = roundsCompleted,
                    maxRounds = maxRounds,
                    lookForBestScore = false,
                    incomingDownfloaters = s2.subList(maxPairs, s2.size).toMutableList().also{it.addAll(limbo)}.sorted()
                )
            )

            if(!compatibleWithLowerBrackets){
                continue
            }
        }

        if(!lookForBestScore){
            return true
        }

        score.updateHiScore(s1, s2)

        if (score.currentTotal == PairingAssessmentCriteria()){
            return true
        }

    }while(nextPermutation(list = s2, changedIndices = changedIndices, length = maxPairs))
    return score.isValidCandidate
}