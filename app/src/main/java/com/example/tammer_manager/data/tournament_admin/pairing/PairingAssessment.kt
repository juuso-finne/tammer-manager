package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer

fun passesAbsoluteCriteria(
    pairing: List<Pair<RegisteredPlayer, RegisteredPlayer>>,
    completedRounds:Int,
    colorPreferenceMap: Map<Int, ColorPreference>,
    isFinalRound: Boolean = false
): Boolean{
    return false
}