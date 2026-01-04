package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import kotlin.math.abs

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

/**Minimise the number of topscorers or topscorers' opponents who get a colour difference higher than +2 or lower than -2*/
fun topScorerOrOpponentColorImbalance(
    candidate: Pair<RegisteredPlayer, RegisteredPlayer>,
    roundsCompleted: Int,
    maxRounds: Int,
    colorPreferenceMap: Map<Int, ColorPreference>
): Boolean{

    if (roundsCompleted + 1 < maxRounds){
        return false
    }

    val playerA = candidate.first
    val playerB = candidate.second

    if(!playerA.isTopScorer(roundsCompleted) && !playerB.isTopScorer(roundsCompleted)){
        return false
    }

    val preferenceA = colorPreferenceMap[playerA.id] ?: ColorPreference()
    val preferenceB = colorPreferenceMap[playerB.id] ?: ColorPreference()

    if (preferenceA.strength == ColorPreferenceStrength.NONE || preferenceB.strength == ColorPreferenceStrength.NONE){
        return false
    }

    var assignedColorA: PlayerColor? = null
    var assignedColorB: PlayerColor? = null

    if (preferenceA > preferenceB){
        assignedColorA = preferenceA.preferredColor
        assignedColorB = preferenceA.preferredColor?.reverse()
    } else{
        assignedColorB = preferenceB.preferredColor
        assignedColorA = preferenceB.preferredColor?.reverse()
    }

    val newBalanceA = abs(preferenceA.colorBalance + (assignedColorA?.balance ?: 0))
    val newBalanceB = abs(preferenceB.colorBalance + (assignedColorB?.balance ?: 0))

    return (newBalanceA > 2 || newBalanceB > 2)
}

/**Minimise the number of topscorers or topscorers' opponents who get the same colour three times in a row.*/
fun topScorersOrOpponentsColorstreak(
    candidate: Pair<RegisteredPlayer, RegisteredPlayer>,
    roundsCompleted: Int,
    colorPreferenceMap: Map<Int, ColorPreference>,
    maxRounds: Int,
): Boolean{
    return true
}

/**Minimise the number of players who do not get their colour preference.*/
fun colorpreferenceConflict(
    candidate: Pair<RegisteredPlayer, RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>
): Boolean{
    return true
}

/**Minimise the number of players who do not get their strong colour preference.*/
fun strongColorpreferenceConflict(
    candidate: Pair<RegisteredPlayer, RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>
): Boolean{
    return true
}