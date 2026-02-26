package com.example.tammer_manager.utils

import com.example.tammer_manager.data.tournament_admin.classes.PairingList
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor

fun simulateRound(pairs: PairingList, players: List<RegisteredPlayer>, round: Int){
    pairs.forEach { pair ->
        val scoreWhite = listOf(0f,0f,0f,0f,.5f,1f,1f,1f,1f,1f).random()
        val scoreBlack = 1f - scoreWhite

        val whitePlayerId = pair[PlayerColor.WHITE]?.playerID
        val blackPlayerId = pair[PlayerColor.BLACK]?.playerID

        val playerWhite = players.find { it.id == whitePlayerId }
        val playerBlack = players.find { it.id == blackPlayerId }

        simulateMatch(playerWhite!!, playerBlack, scoreWhite, scoreBlack, round)
    }
}