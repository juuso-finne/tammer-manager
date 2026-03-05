package com.example.tammer_manager.data.tournament_admin.enums

import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer

enum class TieBreak (val abbreviation: String) {
    BUCHHOLZ(abbreviation = "BH"){
        override fun calculate(player: RegisteredPlayer, players: List<RegisteredPlayer>): Float {
            TODO("Not yet implemented")
        }
        override fun toString(): String{ return "Buchholz" }
    },

    MEDIAN_BUCHHOLZ(abbreviation = "MB"){
        override fun calculate(player: RegisteredPlayer, players: List<RegisteredPlayer>): Float {
            TODO("Not yet implemented")
        }
        override fun toString(): String{ return "Median-Buchholz" }
    },

    SONNEBORN_BERGER(abbreviation = "SB"){
        override fun calculate(player: RegisteredPlayer, players: List<RegisteredPlayer>): Float {
            TODO("Not yet implemented")
        }
        override fun toString(): String{ return "Sonneborn-Berger" }
    },

    WINS(abbreviation = "W"){
        override fun calculate(player: RegisteredPlayer, players: List<RegisteredPlayer>): Float {
            TODO("Not yet implemented")
        }
        override fun toString(): String { return "Wins" }
    }
    ;
    abstract fun calculate(player: RegisteredPlayer, players: List<RegisteredPlayer>): Float
}