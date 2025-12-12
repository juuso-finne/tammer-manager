package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.classes.PairingList
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor

fun pairingsPlaceholder(players: List<RegisteredPlayer>):PairingList{
    val output = mutableListOf<Pairing>()
    val sortedPlayerList = players.sortedByDescending { it.score }

    for(i in 0..< sortedPlayerList.size - 1 step 2){
        val playerA = sortedPlayerList[i]
        val playerB = sortedPlayerList[i+1]

        val pairing = mapOf<PlayerColor, HalfPairing>(
    Pair(PlayerColor.WHITE, HalfPairing(playerID = playerA.id)),
            Pair(PlayerColor.BLACK, HalfPairing(playerID = playerB.id))
        )

        output.add(pairing)
    }
    return output
}

fun generateSwissPairs(players: List<RegisteredPlayer>): PairingList{
    return pairingsPlaceholder(players)
}