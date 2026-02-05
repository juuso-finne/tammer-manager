package com.example.tammer_manager.data.tournament_admin

import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.utils.generatePlayers
import com.example.tammer_manager.utils.simulateMatch
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.component4
import kotlin.collections.component5

class ReconstructPairingsTest {
    @Test
    fun `reconstruction produces correct pairings`(){
        val players = generatePlayers(5)
        val (
            playerA,
            playerB,
            playerC,
            playerD,
            playerE
        ) = players

        simulateMatch(playerA, playerB, 1f, 0f, 1)
        simulateMatch(playerC, playerD, 0f, 1f, 1)
        simulateMatch(playerE, null, 1f, 0f, 1)

        simulateMatch(playerA, playerC, 0f, 1f, 2)
        simulateMatch(playerE, playerD, .5f, .5f, 2)
        simulateMatch(playerB, null, 1f, 0f, 2)

        assertThat(reconstructPairings(players, 1)).containsExactlyElementsIn(listOf(
            mapOf(
                Pair(PlayerColor.WHITE, HalfPairing(playerA.id, 1f)),
                Pair(PlayerColor.BLACK, HalfPairing(playerB.id, 0f))
            ),

            mapOf(
                Pair(PlayerColor.WHITE, HalfPairing(playerC.id, 0f)),
                Pair(PlayerColor.BLACK, HalfPairing(playerD.id, 1f))
            ),

            mapOf(
                Pair(PlayerColor.WHITE, HalfPairing(playerE.id, 1f)),
                Pair(PlayerColor.BLACK, HalfPairing(null, null))
            ),
        ))

        assertThat(reconstructPairings(players, 2)).containsExactlyElementsIn(listOf(
            mapOf(
                Pair(PlayerColor.WHITE, HalfPairing(playerA.id, 0f)),
                Pair(PlayerColor.BLACK, HalfPairing(playerC.id, 1f))
            ),

            mapOf(
                Pair(PlayerColor.WHITE, HalfPairing(playerE.id, .5f)),
                Pair(PlayerColor.BLACK, HalfPairing(playerD.id, .5f))
            ),

            mapOf(
                Pair(PlayerColor.WHITE, HalfPairing(playerB.id, 1f)),
                Pair(PlayerColor.BLACK, HalfPairing(null, null))
            ),
        ))

    }
}