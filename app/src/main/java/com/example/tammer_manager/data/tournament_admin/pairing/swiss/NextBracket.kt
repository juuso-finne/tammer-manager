package com.example.tammer_manager.data.tournament_admin.pairing.swiss

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import kotlin.math.min

fun nextBracket(
    output: MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>>,
    remainingPlayers: MutableList<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    lookForBestScore: Boolean = true,
    incomingDownfloaters:List<RegisteredPlayer> = listOf(),
    approvedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>> = remainingPlayers.distinctBy { it.score }.associateBy(
        {it.score},
        {mutableSetOf()}
    ),
    disapprovedDownfloaters:Map<Float, MutableSet<Set<RegisteredPlayer>>> = remainingPlayers.distinctBy { it.score }.associateBy(
        {it.score},
        {mutableSetOf()}
    ),

): Boolean{
    val bracketScore = remainingPlayers.first().score

    val onDisapproveList = disapprovedDownfloaters[bracketScore]?.contains(incomingDownfloaters.toSet())
    if (onDisapproveList ?: false){
        return false
    }

    val onApproveList = approvedDownfloaters[bracketScore]?.contains(incomingDownfloaters.toSet())
    if(!lookForBestScore && onApproveList ?: false){
        return true
    }

    val residentPlayers = mutableListOf<RegisteredPlayer>()
    fetchNextBracket(remainingPlayers = remainingPlayers, residentPlayers = residentPlayers)

    val totalPlayers = min(incomingDownfloaters.size + residentPlayers.size, residentPlayers.size * 2)

    if(residentPlayers.size == 1 && incomingDownfloaters.isEmpty() && remainingPlayers.isNotEmpty()){
        return nextBracket(
            output = output,
            remainingPlayers = if (lookForBestScore) remainingPlayers else remainingPlayers.toMutableList(),
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            lookForBestScore = lookForBestScore,
            incomingDownfloaters = listOf(residentPlayers.first()),
            approvedDownfloaters = approvedDownfloaters,
            disapprovedDownfloaters = disapprovedDownfloaters
        )
    }

    for (maxPairs in totalPlayers/2 downTo 0){
        if(pairHeterogenousBracket(
                output = output,
                remainingPlayers = remainingPlayers,
                residentPlayers = residentPlayers,
                colorPreferenceMap = colorPreferenceMap,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds,
                maxPairs = maxPairs,
                lookForBestScore = lookForBestScore,
                incomingDownfloaters = incomingDownfloaters,
                approvedDownfloaters = approvedDownfloaters,
                disapprovedDownfloaters = disapprovedDownfloaters
        )){
            return true
        }
        if(remainingPlayers.isEmpty()){
            return false
        }
    }
    return false
}

fun fetchNextBracket(
    remainingPlayers: MutableList<RegisteredPlayer>,
    residentPlayers: MutableList<RegisteredPlayer>
){
    val bracketScore = remainingPlayers.first().score

    while(!remainingPlayers.isEmpty()){
        if (remainingPlayers.first().score != bracketScore){
            return
        }
        remainingPlayers.removeAt(0).also{residentPlayers.add(it)}
    }
}