package com.example.tammer_manager.data.tournament_admin.enums

import androidx.annotation.StringRes
import com.example.tammer_manager.R

enum class TournamentType(@param:StringRes val label: Int) {
    SWISS(R.string.tournament_type_swiss),
    ROUND_ROBIN(R.string.tournament_type_round_robin),

    DOUBLE_ROUND_ROBIN(R.string.tournament_type_double_round_robin)
}