package com.example.tammer_manager.data.tournament_admin.pairing

import com.example.tammer_manager.data.tournament_admin.classes.ColorPreference
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer

fun nextBracket(
    remainingPlayers: MutableList<RegisteredPlayer>,
    colorPreferenceMap: Map<Int, ColorPreference>,
    roundsCompleted: Int,
    maxRounds: Int,
    lookForBestScore: Boolean = true,
    incomingDownfloaters:List<RegisteredPlayer> = listOf()
): Boolean{
    return true
}