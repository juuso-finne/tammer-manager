package com.example.tammer_manager.data.tournament_admin.pairing.fast_swiss

import com.example.tammer_manager.data.combinatorics.IndexSwaps
import com.example.tammer_manager.data.combinatorics.applyIndexSwap
import com.example.tammer_manager.data.combinatorics.nextPermutation
import com.example.tammer_manager.data.combinatorics.setupPermutationSkip
import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.data.tournament_admin.pairing.swiss.fetchNextBracket
import com.example.tammer_manager.data.tournament_admin.pairing.swiss.firstIneligiblePair

fun fastNextBracket(
    output: MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>>,
    remainingPlayers: MutableList<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    incomingDownfloaters:List<RegisteredPlayer> = listOf()
): Boolean{
    val residentPlayers = mutableListOf<RegisteredPlayer>()
    fetchNextBracket(remainingPlayers = remainingPlayers, residentPlayers = residentPlayers)

    if (residentPlayers.size == 1 && incomingDownfloaters.isEmpty()){
        if (remainingPlayers.isEmpty()){
            output.add(Pair(residentPlayers[0], null))
            return true
        }

        return fastNextBracket(
            output = output,
            remainingPlayers = remainingPlayers,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds
        )
    }

    val maxPairs = (residentPlayers.size + incomingDownfloaters.size) / 2

    val majority = mutableListOf<RegisteredPlayer>()
    val minority = mutableListOf<RegisteredPlayer>()

    balancePlayerList(
        majority = majority,
        minority = minority,
        originalList = residentPlayers.plus(incomingDownfloaters),
        colorPreferenceMap = colorPreferenceMap
    )

    for (i in maxPairs downTo 0){
        if (
            indexSwitchingLoop(
                output = output,
                majority = majority,
                minority = minority,
                colorPreferenceMap = colorPreferenceMap,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds,
                maxPairs = i,
                remainingPlayers = remainingPlayers
            )
        ){
            return true
        }
        if (remainingPlayers.isEmpty()){
            return false
        }
    }

    return false
}

fun indexSwitchingLoop(
    output: MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>>,
    majority: MutableList<RegisteredPlayer>,
    minority: MutableList<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    maxPairs: Int,
    remainingPlayers: MutableList<RegisteredPlayer>
): Boolean{
    for(next in IndexSwaps(sizeS1 = minority.size, sizeS2 = majority.size).iterator()){
        val swappingIndices = Pair(next.first.copyOf(), next.second.copyOf())

        applyIndexSwap(minority, majority, swappingIndices)

        if(iterationLoop(
                output = output,
                majority = majority.sorted().toMutableList(),
                minority = minority,
                colorPreferenceMap = colorPreferenceMap,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds,
                maxPairs = maxPairs,
                remainingPlayers = remainingPlayers
            )){
            return true
        }

        applyIndexSwap(minority, majority, swappingIndices)
    }
    return false
}

fun iterationLoop(
    output: MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>>,
    majority: MutableList<RegisteredPlayer>,
    minority: MutableList<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    maxPairs: Int,
    remainingPlayers: MutableList<RegisteredPlayer>
): Boolean{
    val isLastBracket = remainingPlayers.isEmpty()
    val pairs = mutableListOf<Pair<RegisteredPlayer, RegisteredPlayer>>()

    val byeInBracket = (majority.size + minority.size) % 2 == 1

    do{
        pairs.clear()
        for (i in 0 until maxPairs){
            if(minority.size > i && majority.size > i){
                pairs.add(Pair(minority[i], majority[i]))
            }
        }

        if(byeInBracket && majority[majority.indices.last].receivedPairingBye){
            continue
        }

        val firstIneligiblePair = firstIneligiblePair(
            pairs = pairs,
            colorPreferenceMap = colorPreferenceMap,
            roundsCompleted = roundsCompleted,
            maxRounds = maxRounds
        )

        // Skip to next iteration if candidate isn't viable
        firstIneligiblePair?.let{
            setupPermutationSkip(
                list = majority,
                i = it,
                length = maxPairs
            )
            continue
        }

        val minorityLeftOver = minority.subList(maxPairs, minority.size).toList()
        val majorityLeftOver = majority.subList(maxPairs, majority.size).toList()

        if(isLastBracket){
            output.addAll(pairs)
            if (byeInBracket){
                output.add(Pair(majority[majority.indices.last], null))
            }
            return true
        }

        if(fastNextBracket(
                output = output,
                remainingPlayers = remainingPlayers,
                colorPreferenceMap = colorPreferenceMap,
                roundsCompleted = roundsCompleted,
                maxRounds = maxRounds,
                incomingDownfloaters = minorityLeftOver.plus(majorityLeftOver)
        )){
            output.addAll(pairs)
            return true
        }
    }while(nextPermutation(majority, maxPairs))

    return false
}

fun balancePlayerList(
    majority: MutableList<RegisteredPlayer>,
    minority: MutableList<RegisteredPlayer>,
    originalList: List<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
){
    val white = originalList.filter { colorPreferenceMap[it.id]?.preferredColor == PlayerColor.WHITE }.toMutableList()
    val black = originalList.filter { colorPreferenceMap[it.id]?.preferredColor == PlayerColor.BLACK }.toMutableList()
    val neutral = originalList.filter { colorPreferenceMap[it.id]?.preferredColor == null }.toMutableList()

    if (white.size > black.size){
        majority.addAll(white)
        minority.addAll(black)
    } else {
        majority.addAll(black)
        minority.addAll(white)
    }

    neutral.sort()

    while(neutral.isNotEmpty() && majority.size > minority.size){
        neutral.removeAt(0).also { minority.add(it) }
    }

    if(neutral.isNotEmpty()){
        minority.addAll(neutral.subList(0, neutral.size/2).toList())
        majority.addAll(neutral.subList(neutral.size/2, neutral.size))
    }

    majority.sortWith (
        compareByDescending<RegisteredPlayer>{ colorPreferenceMap[it.id]?.strength }.
        thenByDescending{ it.score }.
        thenBy { it.tpn }
    )

    while(minority.size < majority.size - 1){
        majority.removeAt(majority.indices.last).also{minority.add(it)}
    }

    majority.sort()
    minority.sort()
}