package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer

fun nextBracket(
    output: MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>>,
    remainingPlayers: MutableList<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    lookForBestScore: Boolean = true,
    incomingDownfloaters:List<RegisteredPlayer> = listOf()
): Boolean{
    val residentPlayers = mutableListOf<RegisteredPlayer>()
    fetchNextBracket(remainingPlayers = remainingPlayers, residentPlayers = residentPlayers)

    val totalPlayers = incomingDownfloaters.size + residentPlayers.size

    if(residentPlayers.size == 1 && incomingDownfloaters.isEmpty() && remainingPlayers.isNotEmpty()){
        return nextBracket(
            output = output,
            remainingPlayers = if (lookForBestScore) remainingPlayers else remainingPlayers.toMutableList(),
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds,
            lookForBestScore = lookForBestScore,
            incomingDownfloaters = listOf(residentPlayers.first())
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
                incomingDownfloaters = incomingDownfloaters
        )){
            return true
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