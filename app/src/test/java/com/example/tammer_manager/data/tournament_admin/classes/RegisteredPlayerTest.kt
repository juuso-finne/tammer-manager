package com.example.tammer_manager.data.tournament_admin.classes

import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class RegisteredPlayerTest {

    lateinit var player: RegisteredPlayer
    lateinit var matchHistoryA: MatchHistory
    lateinit var matchHistoryB: MatchHistory

    @Before
    fun setUp() {
        matchHistoryA = listOf(
            MatchHistoryItem(
                opponentId = 1,
                round = 1,
                result = 1f,
                color = PlayerColor.WHITE
            ),
            MatchHistoryItem(
                opponentId = 1,
                round = 2,
                result = 1f,
                color = PlayerColor.BLACK
            ),
            MatchHistoryItem(
                opponentId = 1,
                round = 3,
                result = 1f,
                color = PlayerColor.WHITE
            ),
            MatchHistoryItem(
                opponentId = null,
                round = 3,
                result = 1f,
                color = PlayerColor.WHITE
            ),
            MatchHistoryItem(
                opponentId = 1,
                round = 4,
                result = 1f,
                color = PlayerColor.WHITE
            ),
        )

        matchHistoryB = listOf(
            MatchHistoryItem(
                opponentId = 1,
                round = 1,
                result = 1f,
                color = PlayerColor.BLACK
            ),
            MatchHistoryItem(
                opponentId = 1,
                round = 2,
                result = 1f,
                color = PlayerColor.WHITE
            ),
            MatchHistoryItem(
                opponentId = 1,
                round = 3,
                result = 1f,
                color = PlayerColor.WHITE
            ),
            MatchHistoryItem(
                opponentId = 1,
                round = 4,
                result = 1f,
                color = PlayerColor.BLACK
            ),
        )

        player = RegisteredPlayer(
            fullName = "X",
            rating = 1500,
            id = 0,
            tpn = 0,
            isActive = true,
            score = 0f,
            matchHistory = matchHistoryA
        )
    }

    @Test
    fun `Color balance returns correct value`(){
        assertThat(player.getColorBalance()).isEqualTo(2)
        assertThat(player.copy(matchHistory = matchHistoryB).getColorBalance()).isEqualTo(0)
    }

    @Test
    fun `Same color in last n matches returns correct value`(){
        assertThat(player.sameColorInLastNRounds(2)).isTrue()
        assertThat(player.sameColorInLastNRounds(3)).isFalse()
        assertThat(player.copy(matchHistory = matchHistoryB).sameColorInLastNRounds(2)).isFalse()
    }

}