package com.example.tammer_manager.utils

import com.example.tammer_manager.data.tournament_admin.classes.MatchHistoryItem
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor

fun simulateMatch(playerWhite: RegisteredPlayer, playerBlack: RegisteredPlayer?, scoreWhite: Float, scoreBlack: Float, round: Int){
    playerWhite.matchHistory = playerWhite.matchHistory.plusElement(MatchHistoryItem(
        opponentId = playerBlack?.id,
        round = round,
        result = scoreWhite,
        color = PlayerColor.WHITE
    ))
    playerWhite.score += scoreWhite

    playerBlack?.matchHistory = playerBlack.matchHistory.plusElement(MatchHistoryItem(
        opponentId = playerWhite.id,
        round = round,
        result = scoreBlack,
        color = PlayerColor.BLACK
    ))
    playerBlack?.score += scoreBlack
}
