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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.data.player_import.ImportedPlayer
import com.example.tammer_manager.ui.screens.enter_players.TextRow
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.PlayerPoolViewModel
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun PlayerPoolContainer(
    navController: NavController,
    vmPlayerPool: PlayerPoolViewModel,
    vmTournament: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val (searchTerm, setSearchTerm) = remember{ mutableStateOf("") }

        PlayerPoolHeader()
        HorizontalDivider()

        PlayerPool(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            players = vmPlayerPool.playerPool.collectAsState().value.filter(){ p ->
                Regex(
                    pattern = Regex.escape(searchTerm),
                    option= RegexOption.IGNORE_CASE
                ).containsMatchIn(p.fullName)
            },
            vmTournament = vmTournament
        )
        SearchBar(
            searchTerm = searchTerm,
            setSearchTerm = setSearchTerm
        )

        Button(onClick = {navController.navigate("playerImport")}) { Text("Import player list")}
    }
}

@Composable
fun PlayerPool(
    players: List<ImportedPlayer>,
    vmTournament: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        LazyColumn() {
            items(players.size) { i ->
                PlayerPoolItem(
                    player =  players[i],
                    vmTournament = vmTournament,
                )
                HorizontalDivider()
            }
        }
    }
}



@Composable
fun PlayerPoolHeader(modifier: Modifier = Modifier) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(end = 58.dp), verticalAlignment = Alignment.CenterVertically) {
        TextRow(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 5.dp),
            style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            texts = listOf("Name", "Rating")
        )
    }
}

@Composable
fun PlayerPoolItem(
    player: ImportedPlayer,
    vmTournament: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val playerRegistered = vmTournament.registeredPlayers.collectAsState().value.any(){
            p -> p.fullName == player.fullName && p.rating == player.rating
    }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TextRow(
            modifier = Modifier.weight(1f),
            texts = listOf(player.fullName, player.rating.toString())
        )

        Row(
            modifier = Modifier.padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { vmTournament.addPlayer(player) },
                enabled = !playerRegistered
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    tint = if (playerRegistered) Color.DarkGray else Color.Blue,
                    contentDescription = "Add player to tournament"
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    searchTerm: String,
    setSearchTerm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = Modifier.padding(vertical = 5.dp),
        value = searchTerm,
        onValueChange = { setSearchTerm(it) },
        label = {Text("Search player")}
    )
}

@Composable
fun ButtonRow(navController: NavController, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ){

    }
}