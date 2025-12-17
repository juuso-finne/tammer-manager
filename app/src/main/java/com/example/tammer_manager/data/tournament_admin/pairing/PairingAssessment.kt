package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength

fun passesAbsoluteCriteria(
    candidate: List<Pair<RegisteredPlayer, RegisteredPlayer>>,
    roundsCompleted:Int,
    colorPreferenceMap: Map<Int, ColorPreference>,
    isFinalRound: Boolean = false
): Boolean{

    candidate.forEach {
        val playerA = it.first
        val playerB = it.second

        val colorPreferenceA = colorPreferenceMap[playerA.id]
        val colorPreferenceB = colorPreferenceMap[playerB.id]

        if(playerA.matchHistory.any(){ match -> match.opponentId == playerB.id }){
            return false
        }

        val isTopScorersMeeting =
            playerA.isTopScorer(roundsCompleted = roundsCompleted) &&
            playerB.isTopScorer(roundsCompleted = roundsCompleted) &&
            isFinalRound

        if (
            !isTopScorersMeeting &&
            colorPreferenceA?.strength == ColorPreferenceStrength.ABSOLUTE &&
            colorPreferenceB?.strength == ColorPreferenceStrength.ABSOLUTE &&
            colorPreferenceA.preferredColor == colorPreferenceB.preferredColor
        ){
            return false
        }
    }

    return true
}

fun assessPairing(candidate: Pair<RegisteredPlayer, RegisteredPlayer>): PairingAssessmentCriteria{
    TODO()
}