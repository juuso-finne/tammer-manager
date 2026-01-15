package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.combinatorics.nextPermutation
import com.example.tammer_manager.data.tournament_admin.classes.CandidateAssessmentScore
import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer

fun pairHomogenousBracket(
    playerList: List<RegisteredPlayer>,
    s1: List<RegisteredPlayer>,
    s2: List<RegisteredPlayer>,
    output: MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>>,
    maxPairs: Int = s1.size/2,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int
): Boolean{
    val s2Copy = s2.sorted().toMutableList()

    val changedIndices = mutableListOf<Int>()
    val score = CandidateAssessmentScore()
    val isLastBracket = playerList.isEmpty()
    val downFloaters = mutableListOf<RegisteredPlayer>()

    do{
        assessCandidate(
            s1 = s1,
            s2 = s2Copy,
            changedIndices = changedIndices,
            score = score,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds
        )

        if(!score.isValidCandidate){
            continue
        }

        if(score.currentTotal == PairingAssessmentCriteria()){

        }
    }while(nextPermutation(list = s2Copy, changedIndices = changedIndices, length = maxPairs))
    return true
}