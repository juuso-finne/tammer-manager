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
    remainingPlayers: MutableList<RegisteredPlayer>,
    residentPlayers: List<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    maxPairs: Int,
    lookForBestScore: Boolean,
    incomingDownfloaters:List<RegisteredPlayer> = listOf()
):Boolean{
    val mdpsToPair = min(incomingDownfloaters.size, maxPairs)
    val s1 = incomingDownfloaters.take(mdpsToPair).toMutableList()
    val limbo = incomingDownfloaters.takeLast(incomingDownfloaters.size - mdpsToPair).toMutableList()

    val s2 = residentPlayers.toMutableList()


    val mdpPairingScore = CandidateAssessmentScore()
    if(s1.isEmpty()){
        mdpPairingScore.bestTotal = PairingAssessmentCriteria()
        mdpPairingScore.isValidCandidate = true
    }


    for(next in IndexSwaps(sizeS1 = s1.size, sizeS2 = s2.size)){
        val swappingIndices = Pair(next.first.copyOf(), next.second.copyOf())
        applyIndexSwap(s1, s2, swappingIndices)
        val s2Copy = s2.sorted().toMutableList()
        if (iterateS2(
                remainingPlayers = remainingPlayers,
                s1 = s1,
                s2 = s2Copy,
                colorPreferenceMap = colorPreferenceMap,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds,
                maxPairs = mdpsToPair,
                limbo = limbo,
                score = mdpPairingScore,
                lookForBestScore = lookForBestScore,
                checkCompatibility = false
            )){

            val remainder = s2Copy.subList(mdpsToPair, s2Copy.size)
            val remainderPairs = (maxPairs - mdpsToPair)
            val remainderPairingScore = CandidateAssessmentScore()
        }
        applyIndexSwap(s1, s2, swappingIndices)
    }
    return false
}