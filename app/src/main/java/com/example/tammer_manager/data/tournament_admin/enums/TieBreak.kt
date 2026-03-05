package com.example.tammer_manager.data.tournament_admin.enums

import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import kotlin.math.max

enum class TieBreak (val abbreviation: String) {
    BUCHHOLZ(abbreviation = "BH"){

        override fun calculate(player: RegisteredPlayer, players: List<RegisteredPlayer>): Float {
            val oppnentIds = player.matchHistory.mapNotNull { it.opponentId }
            return oppnentIds.fold(0f){ acc, id -> acc + (players.find { it.id == id }?.score ?: 0f) }
        }

        override fun toString(): String{ return "Buchholz" }
    },

    MEDIAN_BUCHHOLZ(abbreviation = "MB"){

        override fun calculate(player: RegisteredPlayer, players: List<RegisteredPlayer>): Float {
            val oppnentIds = player.matchHistory.mapNotNull { it.opponentId }
            val opponentScores = oppnentIds.mapNotNull { players.find { player -> player.id == it }?.score }
            val total = opponentScores.sum() - opponentScores.max() - opponentScores.min()
            return max(0f, total)
        }
        override fun toString(): String{ return "Median-Buchholz" }
    },

    SONNEBORN_BERGER(abbreviation = "SB"){

        override fun calculate(player: RegisteredPlayer, players: List<RegisteredPlayer>): Float {
            var sum = 0f

            for (i in player.matchHistory.indices) {
                val item = player.matchHistory[i]

                if(item.opponentId == null || item.result == 0f){
                    continue
                }

                val opponent = players.find { it.id == item.opponentId }
                sum += item.result * (opponent?.score ?: 0f)
            }

            return sum
        }

        override fun toString(): String{ return "Sonneborn-Berger" }
    },

    WINS(abbreviation = "W"){

        override fun calculate(player: RegisteredPlayer, players: List<RegisteredPlayer>): Float {
            return 1f * player.matchHistory.count { it.opponentId != null && it.result == 1f }
        }

        override fun toString(): String { return "Wins" }
    }
    ;
    abstract fun calculate(player: RegisteredPlayer, players: List<RegisteredPlayer>): Float
}