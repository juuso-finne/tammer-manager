package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.utils.generatePlayers
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GenerateRoundRobinPairsTest {
    @Test
    fun `Function generates correct round robin pairs for an even number of players`(){
        val players = generatePlayers(4).sorted()

        val (playerA, playerB, playerC, playerD) = players

        val pairings = mutableListOf<Pairing>()

        generateRoundRobinPairs(players = players, output = pairings, roundsCompleted = 0)

        assertThat(pairings).containsExactlyElementsIn(
            listOf(
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerA.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerC.id))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerD.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerB.id))
                )
            )
        )

        pairings.clear()

        generateRoundRobinPairs(players = players, output = pairings, roundsCompleted = 1)

        assertThat(pairings).containsExactlyElementsIn(
            listOf(
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerD.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerA.id))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerB.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerC.id))
                )
            )
        )

        pairings.clear()

        generateRoundRobinPairs(players = players, output = pairings, roundsCompleted = 2)

        assertThat(pairings).containsExactlyElementsIn(
            listOf(
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerA.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerB.id))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerC.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerD.id))
                )
            )
        )
    }

    @Test
    fun `Function generates correct round robin pairs for an odd number of players`(){
        val players = generatePlayers(5).sorted()

        val (playerA, playerB, playerC, playerD, playerE) = players

        val pairings = mutableListOf<Pairing>()

        generateRoundRobinPairs(players = players, output = pairings, roundsCompleted = 0)

        assertThat(pairings).containsExactlyElementsIn(
            listOf(
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerC.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(null,0f))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerA.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerD.id))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerE.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerB.id))
                )
            )
        )

        pairings.clear()

        generateRoundRobinPairs(players = players, output = pairings, roundsCompleted = 1)

        assertThat(pairings).containsExactlyElementsIn(
            listOf(
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerD.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(null,0f))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerC.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerE.id))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerB.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerA.id))
                )
            )
        )

        pairings.clear()

        generateRoundRobinPairs(players = players, output = pairings, roundsCompleted = 2)

        assertThat(pairings).containsExactlyElementsIn(
            listOf(
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerE.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(null,0f))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerD.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerB.id))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerA.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerC.id))
                )
            )
        )

        pairings.clear()

        generateRoundRobinPairs(players = players, output = pairings, roundsCompleted = 3)

        assertThat(pairings).containsExactlyElementsIn(
            listOf(
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerB.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(null,0f))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerE.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerA.id))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerC.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerD.id))
                )
            )
        )

        pairings.clear()

        generateRoundRobinPairs(players = players, output = pairings, roundsCompleted = 4)

        assertThat(pairings).containsExactlyElementsIn(
            listOf(
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerA.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(null,0f))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerB.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerC.id))
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerD.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerE.id))
                )
            )
        )
    }
}