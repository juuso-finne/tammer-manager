package com.example.tammer_manager.data.tournament_admin.classes

import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class RegisteredPlayerTest {

    lateinit var player: RegisteredPlayer
    lateinit var matchHistoryA: MatchHistory
    lateinit var matchHistoryB: MatchHistory
    lateinit var matchHistoryC: MatchHistory

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
            )
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
            )
        )

        matchHistoryC = listOf(
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

    @Test
    fun `Color preference returns correct value`(){
        val playerA = player.copy(matchHistory = matchHistoryA)
        val playerB = player.copy(matchHistory = matchHistoryB)
        val playerC = player.copy(matchHistory = matchHistoryC)

        assertThat(playerA.getColorPreference()).isEqualTo(ColorPreference(
            preferredColor = PlayerColor.BLACK,
            colorBalance = 2,
            strength = ColorPreferenceStrength.ABSOLUTE
        ))

        assertThat(playerB.getColorPreference()).isEqualTo(ColorPreference(
            preferredColor = PlayerColor.WHITE,
            colorBalance = 0,
            strength = ColorPreferenceStrength.MILD
        ))

        assertThat(playerC.getColorPreference()).isEqualTo(ColorPreference(
            preferredColor = PlayerColor.WHITE,
            colorBalance = 1,
            strength = ColorPreferenceStrength.STRONG
        ))

        assertThat(player.copy(matchHistory = listOf()).getColorPreference()).isEqualTo(
            ColorPreference())
    }

}