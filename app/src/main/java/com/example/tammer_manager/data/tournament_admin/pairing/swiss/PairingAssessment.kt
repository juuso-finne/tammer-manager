package com.example.tammer_manager.data.tournament_admin.pairing.swiss

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.PairingAssessmentCriteria
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import kotlin.math.abs

fun passesAbsoluteCriteria(
    candidate: Pair<RegisteredPlayer, RegisteredPlayer>,
    roundsCompleted:Int,
    colorPreferenceMap: Map<Int, ColorPreference>,
    isFinalRound: Boolean = false
): Boolean{


    val playerA = candidate.first
    val playerB = candidate.second

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


    return true
}

fun assessPairing(
    candidate: Pair<RegisteredPlayer, RegisteredPlayer>,
    roundsCompleted: Int,
    maxRounds: Int,
    colorPreferenceMap: Map<Int, ColorPreference>
): PairingAssessmentCriteria{
    val output = PairingAssessmentCriteria()

    if (
        topScorerOrOpponentColorImbalance(
            candidate,
            roundsCompleted,
            maxRounds,
            colorPreferenceMap
        )
    ){
        output.topScorerOrOpponentColorImbalanceCount++
    }

    if (
        topScorersOrOpponentsColorStreak(
            candidate,
            roundsCompleted,
            colorPreferenceMap,
            maxRounds
        )
    ) {
        output.topScorersOrOpponentsColorstreakCount++
    }

    if (
        colorpreferenceConflict(
            candidate,
            colorPreferenceMap
        )
    ) {
        output.colorpreferenceConflicts++
    }

    if (
        strongColorpreferenceConflict(
            candidate,
            colorPreferenceMap
        )
    ) {
        output.strongColorpreferenceConflicts++
    }

    return output
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

    var assignedColorA: PlayerColor?
    var assignedColorB: PlayerColor?

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
fun topScorersOrOpponentsColorStreak(
    candidate: Pair<RegisteredPlayer, RegisteredPlayer>,
    roundsCompleted: Int,
    colorPreferenceMap: Map<Int, ColorPreference>,
    maxRounds: Int,
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

    if(preferenceA.preferredColor != preferenceB.preferredColor){
        return false
    }

    return playerA.sameColorInLastNRounds(2) && playerB.sameColorInLastNRounds(2)
}

/**Minimise the number of players who do not get their colour preference.*/
fun colorpreferenceConflict(
    candidate: Pair<RegisteredPlayer, RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>
): Boolean{
    val playerA = candidate.first
    val playerB = candidate.second

    val preferenceA = colorPreferenceMap[playerA.id] ?: ColorPreference()
    val preferenceB = colorPreferenceMap[playerB.id] ?: ColorPreference()

    if (preferenceA.strength == ColorPreferenceStrength.NONE || preferenceB.strength == ColorPreferenceStrength.NONE){
        return false
    }

    return preferenceA.preferredColor == preferenceB.preferredColor
}

/**Minimise the number of players who do not get their strong colour preference.*/
fun strongColorpreferenceConflict(
    candidate: Pair<RegisteredPlayer, RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>
): Boolean{
    val playerA = candidate.first
    val playerB = candidate.second

    val preferenceA = colorPreferenceMap[playerA.id] ?: ColorPreference()
    val preferenceB = colorPreferenceMap[playerB.id] ?: ColorPreference()


    if (preferenceA < ColorPreference(strength = ColorPreferenceStrength.STRONG) || preferenceB < ColorPreference(strength = ColorPreferenceStrength.STRONG)){
        return false
    }

    return preferenceA.preferredColor == preferenceB.preferredColor
}