package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tammer_manager.data.player_import.ImportedPlayer
import com.example.tammer_manager.viewmodels.PlayerPoolViewModel

@Composable
fun CreateTournament(
    vmPlayerPool: PlayerPoolViewModel,
    modifier: Modifier = Modifier
) {
    PlayerPool(vmPlayerPool.playerPool.collectAsState().value)
}

@Composable
fun PlayerPool(
    players: List<ImportedPlayer>,
    modifier: Modifier = Modifier
) {
    LazyColumn() {
        items(players.size){ i ->
            val player = players[i]
            Row(horizontalArrangement = Arrangement.Center){
                Text(player.fullName)
                Spacer(Modifier.width(12.dp))
                Text(player.rating.toString())
            }
        }
    }
}