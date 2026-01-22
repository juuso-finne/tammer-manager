package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.utils.generatePlayers
import com.example.tammer_manager.utils.simulateMatch
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class AssignColorsTest {
    lateinit var playerA: RegisteredPlayer
    lateinit var playerB: RegisteredPlayer
    lateinit var playerC: RegisteredPlayer
    lateinit var playerD: RegisteredPlayer
    lateinit var playerE: RegisteredPlayer
    lateinit var playerF: RegisteredPlayer
    lateinit var playerG: RegisteredPlayer
    lateinit var playerH: RegisteredPlayer
    lateinit var playerI: RegisteredPlayer

    lateinit var players: MutableList<RegisteredPlayer>

    lateinit var colorPreferenceMap: Map<Int, ColorPreference>

    @Before
    fun setup(){
        players = generatePlayers(9).toMutableList()

        players.sort()

        val (a,b,c,d,e) = players

        playerA = a
        playerB = b
        playerC = c
        playerD = d
        playerE = e

        val (f,g,h,i) = players.subList(5, 9)

        playerF = f
        playerG = g
        playerH = h
        playerI = i

        simulateMatch(playerE, playerA, 0f, 1f, 1)
        simulateMatch(playerF, playerB, 1f, 0f, 1)
        simulateMatch(playerC, playerG, .5f, .5f, 1)
        simulateMatch(playerH, playerD, 0f, 1f, 1)
        simulateMatch(playerI, null, 1f, 0f, 1)
        playerI.receivedPairingBye = true

        simulateMatch(playerA, playerF, 0f, 1f, 2)
        simulateMatch(playerD, playerI, 1f, 0f, 2)
        simulateMatch(playerB, playerC, 1f, 0f, 2)
        simulateMatch(playerG, playerE, 1f, 0f, 2)
        simulateMatch(playerH, null, 1f, 1f, 2)
        playerH.receivedPairingBye = true

        colorPreferenceMap = players.associateBy(
            {it.id},
            {it.getColorPreference()}
        )
    }

    @Test
    fun `Colors are assigned correctly`(){
        val playerpairs = listOf(
            Pair(playerD, playerF),
            Pair(playerA, playerG),
            Pair(playerI, playerB),
            Pair(playerC, playerH),
            Pair(playerE, null)
        )

        val pairings = playerpairs.map { assignColors(it, colorPreferenceMap) }

        assertThat(pairings).containsExactlyElementsIn(
            listOf(
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerF.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerD.id)),
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerA.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerG.id)),
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerI.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerB.id)),
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerC.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(playerH.id)),
                ),
                mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(playerE.id)),
                    Pair(PlayerColor.BLACK, HalfPairing()),
                ),
            )
        )

    }
}