package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength

fun passesAbsoluteCriteria(
    pairing: List<Pair<RegisteredPlayer, RegisteredPlayer>>,
    roundsCompleted:Int,
    colorPreferenceMap: Map<Int, ColorPreference>,
    isFinalRound: Boolean = false
): Boolean{

    pairing.forEach {
        val playerA = it.first
        val playerB = it.second

        val colorPreferenceA = colorPreferenceMap[playerA.id]
        val colorPreferenceB = colorPreferenceMap[playerB.id]

        if(playerA.matchHistory.any(){ match -> match.opponentId == playerB.id }){
            return false
        }

        if (
            isFinalRound &&
            playerA.isTopScorer(roundsCompleted = roundsCompleted) &&
            playerB.isTopScorer(roundsCompleted = roundsCompleted) &&
            colorPreferenceA?.strength == ColorPreferenceStrength.ABSOLUTE &&
            colorPreferenceB?.strength == ColorPreferenceStrength.ABSOLUTE &&
            colorPreferenceA.preferredColor == colorPreferenceB.preferredColor
        ){
            return false
        }
    }

    return true
}