package com.example.tammer_manager.data.tournament_admin.enums

enum class TournamentType {
    SWISS {
        override fun toString(): String {
            return "Swiss"
        }
    },
    ROUND_ROBIN {
        override fun toString(): String {
            return "Round Robin"
        }
    }
}