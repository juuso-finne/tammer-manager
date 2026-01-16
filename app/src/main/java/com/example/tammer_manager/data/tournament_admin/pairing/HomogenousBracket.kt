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
    maxPairs: Int = s1.size/2,
    score: CandidateAssessmentScore,
    lookForBestScore: Boolean
): Boolean{
    for(next in IndexSwaps(sizeS1 = s1.size, sizeS2 = s2.size)){
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
            lookForBestScore = lookForBestScore
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
    lookForBestScore: Boolean
): Boolean{

    var foundViableCandidate = false
    val changedIndices = mutableListOf<Int>()
    val isLastBracket = remainingPlayers.isEmpty()
    val byeInBracket = isLastBracket && s2.size % 2 == 1

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

        val compatibleWithLowerBrackets = (
            isLastBracket ||
            nextBracket(
                remainingPlayers = remainingPlayers.toMutableList(),
                colorPreferenceMap = colorPreferenceMap,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds,
                lookForBestScore = false,
                incomingDownfloaters = s2.subList(maxPairs, s2.size).toMutableList().also{it.addAll(limbo)}
            )
        )

        if(!compatibleWithLowerBrackets){
            continue
        }

        foundViableCandidate = true

        if(!lookForBestScore){
            return true
        }

        score.bestTotal = score.currentTotal
        score.bestCandidate.clear()

        for (i in 0 until s1.size){
            score.bestCandidate.add(Pair(s1[i], s2[i]))
        }

        if (score.currentTotal == PairingAssessmentCriteria()){
            return true
        }

    }while(nextPermutation(list = s2, changedIndices = changedIndices, length = maxPairs))
    return foundViableCandidate
}