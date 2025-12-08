package com.example.tammer_manager.ui.screens.enter_players.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tammer_manager.data.tournament_admin.RegisteredPlayer
import com.example.tammer_manager.ui.screens.enter_players.TextRow
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun RegisteredPlayerContainer(
    vmTournament: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier){

        RegisteredHeader()
        RegisteredPlayerList(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            players = vmTournament.registeredPlayers.collectAsState().value,
            vmTournament = vmTournament
        )
    }
}

@Composable
fun RegisteredPlayerList(
    players: List<RegisteredPlayer>,
    vmTournament: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    Box(modifier){
        LazyColumn() {
            items(players.size){i->
                RegisteredPlayerItem(
                    player =  players[i],
                    vmTournament = vmTournament,
                    index = i
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun RegisteredHeader(modifier: Modifier = Modifier) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(end = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        TextRow(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 5.dp),
            style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            texts = listOf("Name", "Rating", "Active")
        )
    }
}

@Composable
fun RegisteredPlayerItem(
    player: RegisteredPlayer,
    vmTournament: TournamentViewModel,
    index :Int,
    modifier: Modifier = Modifier) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TextRow(
            modifier = Modifier.weight(1f),
            style = Typography.bodyLarge,
            texts = listOf(player.fullName, player.rating.toString())
        )

        Row(
            modifier = Modifier.padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if(player.isActive){
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Player is active",
                    tint = Color.Green
                )
            }else{
                Icon (
                    imageVector = Icons.Default.Close,
                    tint = Color.Red,
                    contentDescription = "Player is NOT active"
                )
            }

            IconButton(onClick = {
                if (player.isActive){
                    vmTournament.removePlayer(index)
                } else{
                    vmTournament.activatePlayer(index)
                }
            }) {
                if(player.isActive){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove player",
                        tint = Color.Blue
                    )
                }else{
                    Icon (
                        imageVector = Icons.Default.Add,
                        tint = Color.Blue,
                        contentDescription = "Reactivate player"
                    )
                }
            }
        }
    }
}