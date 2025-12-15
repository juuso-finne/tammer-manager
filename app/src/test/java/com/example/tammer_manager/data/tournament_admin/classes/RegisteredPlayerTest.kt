package com.example.tammer_manager.data.tournament_admin.classes

import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class RegisteredPlayerTest {

    lateinit var player: RegisteredPlayer
    @Before
    fun setUp() {
        player = RegisteredPlayer(
            fullName = "X",
            rating = 1500,
            id = 0,
            tpn = 0,
            isActive = true,
            score = 0f,
            matchHistory = listOf(
                MatchHistoryItem(
                    opponentId = 1,
                    round = 1,
                    result = 1f,
                    color = PlayerColor.WHITE
                ),                MatchHistoryItem(
                    opponentId = 1,
                    round = 1,
                    result = 1f,
                    color = PlayerColor.BLACK
                ),                MatchHistoryItem(
                    opponentId = 1,
                    round = 1,
                    result = 1f,
                    color = PlayerColor.WHITE
                ),                MatchHistoryItem(
                    opponentId = 1,
                    round = 1,
                    result = 1f,
                    color = PlayerColor.WHITE
                ),
            ),
        )
    }

    @Test
    fun `Method getColorBalance returns correct value`(){
        assertThat(player.getColorBalance()).isEqualTo(2)
    }

}