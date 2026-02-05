package com.example.tammer_manager.data.tournament_admin

import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.classes.PairingList
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor

/**
 * Reconstruct pairings of a given past round
 *
 * @param players all players (active and inactive) of the tournament
 * @param round The round to be reconstructed
 */
fun reconstructPairings(players: List<RegisteredPlayer>, round: Int): PairingList{
    val handledPlayers = mutableListOf<Int>()
    val output = mutableListOf<Pairing>()

    for (i in players.indices){
        val player = players[i]

        if(player.id in handledPlayers){
            continue
        }

        handledPlayers.add(player.id)

        val matchInfo = player.matchHistory.find { it.round == round }
        if (matchInfo == null){
            continue
        }

        val opponentId = matchInfo.opponentId

        if(opponentId == null){
            output.add(mapOf(
                Pair(PlayerColor.WHITE, HalfPairing(player.id, 1f)),
                Pair(PlayerColor.BLACK, HalfPairing())
            ))

            continue
        }

        handledPlayers.add(opponentId)
        val opponent = players.find { it.id == opponentId }

        val opponentScore = opponent!!.matchHistory.find { it.round == round }!!.result

        output.add(mapOf(
            Pair(matchInfo.color, HalfPairing(player.id, matchInfo.result)),
            Pair(matchInfo.color.reverse(), HalfPairing(opponentId, opponentScore))
        ))
    }

    return output.toList()
}