package com.example.tammer_manager.utils

import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer

fun generatePlayers(amount: Int):List<RegisteredPlayer>{
    val output = mutableListOf<RegisteredPlayer>()
    for (i in 1 ..amount){
        output.add(RegisteredPlayer(
            fullName = "Player $i",
            rating = 1000 + 100 * (amount - i + 1),
            id = i,
            tpn = i
        ))
    }
    return output
}