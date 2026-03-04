package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun SplitTournament(
    vmTournament: TournamentViewModel,
    navController: NavController
){
    val (players, setplayers) = remember { mutableStateOf(vmTournament.registeredPlayers.value.sortedByDescending { it.rating }) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        Text(text = "Split tournament", style = Typography.headlineMedium)

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
             items(players.size){ i ->
                val player = players[i]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text ="${player.fullName} (${player.rating})",
                        style = Typography.bodyLarge,
                        modifier = Modifier.weight(.75f)
                    )

                    TextField(
                        label = {Text("Group")},
                        value = player.group,
                        onValueChange = {
                            val newPlayers = players.toMutableList()
                            newPlayers[i] = newPlayers[i].copy(group = it)
                            setplayers(newPlayers)
                        },
                        modifier = Modifier.weight(.25f)
                    )
                }
            }
        }

        Button(
            enabled =
                players.all{ it.group.isNotEmpty() } &&
                players.map { it.group }.distinct().size > 1
            ,
            onClick = {
                vmTournament.splitTournament(
                    context = context,
                    updatedPlayerList = players
                )

                navController.navigate("home")
            }
        ){
            Text("Split")
        }
        Button({navController.navigate("home")}) { Text("Cancel") }
    }
}
