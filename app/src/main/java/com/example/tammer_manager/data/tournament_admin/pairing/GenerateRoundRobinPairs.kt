package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor

fun generateRoundRobinPairs(
    players: List<RegisteredPlayer>,
    output: MutableList<Pairing>,
    roundsCompleted: Int
){
    val a: MutableList<RegisteredPlayer?> = players.subList(0, players.size/2).toMutableList()
    val b: MutableList<RegisteredPlayer?> = players.subList(players.size/2, players.size).toMutableList()

    val playerParity = players.size % 2

    if(playerParity == 1){
        a.add(0, null)
    }

    repeat(roundsCompleted){
        rotate(a,b)
    }

    for (i in a.indices){
        if(a[i] == null){
            output.add(mapOf(
                Pair(PlayerColor.WHITE, HalfPairing(b[0]!!.id)),
                Pair(PlayerColor.BLACK, HalfPairing(null))
            ))
            continue
        }

        if (i > 0){
            val indexParity = i % 2

            if(indexParity == playerParity){
                output.add(mapOf(
                    Pair(PlayerColor.WHITE, HalfPairing(a[i]!!.id)),
                    Pair(PlayerColor.BLACK, HalfPairing(b[i]!!.id))
                ))
                continue
            }

            output.add(mapOf(
                Pair(PlayerColor.WHITE, HalfPairing(b[i]!!.id)),
                Pair(PlayerColor.BLACK, HalfPairing(a[i]!!.id))
            ))
            continue
        }

        val oddRound = roundsCompleted % 2 == 1
        if (oddRound && playerParity == 0){
            output.add(mapOf(
                Pair(PlayerColor.WHITE, HalfPairing(b[0]!!.id)),
                Pair(PlayerColor.BLACK, HalfPairing(a[0]!!.id))
            ))
            continue
        }

        output.add(mapOf(
            Pair(PlayerColor.WHITE, HalfPairing(a[0]!!.id)),
            Pair(PlayerColor.BLACK, HalfPairing(b[0]!!.id))
        ))
    }
}

fun rotate(a: MutableList<RegisteredPlayer?>, b: MutableList<RegisteredPlayer?>){
    if(a.isNotEmpty()){
        a.removeAt(a.indices.last).also { b.add(it) }
        b.removeAt(0).also { a.add(1, it) }
    }
}