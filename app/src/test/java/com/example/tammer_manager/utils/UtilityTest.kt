package com.example.tammer_manager.utils

import com.example.tammer_manager.data.tournament_admin.classes.MatchHistoryItem
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UtilityTest {

    @Test
    fun `GeneratePlayers produces expected output`(){
        val playerList = generatePlayers(5)
        assertThat(playerList.size).isEqualTo(5)
        assertThat(playerList[0]).isEqualTo(RegisteredPlayer(
            fullName = "Player 1",
            rating = 1500,
            id = 1,
            tpn = 1,
        ))

        assertThat(playerList[2]).isEqualTo(RegisteredPlayer(
            fullName = "Player 3",
            rating = 1300,
            id = 3,
            tpn = 3,
        ))

        assertThat(playerList.last()).isEqualTo(RegisteredPlayer(
            fullName = "Player 5",
            rating = 1100,
            id = 5,
            tpn = 5,
        ))
    }

    @Test
    fun `SimulateMatch produces expected output`(){
        val (playerA, playerB, playerC, playerD) = generatePlayers(4)
        simulateMatch(playerA, playerB, .5f, .5f, 1)
        simulateMatch(playerC, playerD, 1f, 0f, 1)
        simulateMatch(playerB, playerC, 1f, 0f, 2)
        simulateMatch(playerD, playerA, 0f, 1f, 2)
        simulateMatch(playerB, playerD, 0f, 1f, 3)
        simulateMatch(playerA, playerC, .5f, .5f, 3)

        assertThat(playerA.score).isEqualTo(2f)
        assertThat(playerB.score).isEqualTo(1.5f)
        assertThat(playerC.score).isEqualTo(1.5f)
        assertThat(playerD.score).isEqualTo(1f)

        assertThat(playerA.matchHistory).containsExactly(
            MatchHistoryItem(playerB.id, 1, .5f, PlayerColor.WHITE),
            MatchHistoryItem(playerD.id, 2, 1f, PlayerColor.BLACK),
            MatchHistoryItem(playerC.id, 3, .5f, PlayerColor.WHITE),
        ).inOrder()

        assertThat(playerB.matchHistory).containsExactly(
            MatchHistoryItem(playerA.id, 1, .5f, PlayerColor.BLACK),
            MatchHistoryItem(playerC.id, 2, 1f, PlayerColor.WHITE),
            MatchHistoryItem(playerD.id, 3, 0f, PlayerColor.WHITE),
        ).inOrder()

        assertThat(playerC.matchHistory).containsExactly(
            MatchHistoryItem(playerD.id, 1, 1f, PlayerColor.WHITE),
            MatchHistoryItem(playerB.id, 2, 0f, PlayerColor.BLACK),
            MatchHistoryItem(playerA.id, 3, .5f, PlayerColor.BLACK),
        ).inOrder()

        assertThat(playerD.matchHistory).containsExactly(
            MatchHistoryItem(playerC.id, 1, 0f, PlayerColor.BLACK),
            MatchHistoryItem(playerA.id, 2, 0f, PlayerColor.WHITE),
            MatchHistoryItem(playerB.id, 3, 1f, PlayerColor.BLACK),
        ).inOrder()
    }
}