package com.example.tammer_manager.ui.screens

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tammer_manager.data.player_import.ImportedPlayer
import com.example.tammer_manager.data.tournament_admin.RegisteredPlayer
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.PlayerPoolViewModel
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun EnterPlayers(
    vmPlayerPool: PlayerPoolViewModel,
    vmTournament: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val (searchTerm, setSearchTerm) = remember{ mutableStateOf("") }
    Column(
        modifier = Modifier.padding(horizontal = 5.dp)
    ){
        Column(
            modifier = Modifier.weight(1f),
        ){
            Text(
                text = "Imported players",
                style = Typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .fillMaxWidth()
            )
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
        }

        HorizontalDivider(thickness = 2.dp)


        Column(modifier = Modifier.weight(1f)){
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
        placeholder = {Text("Enter name")},
        onValueChange = { setSearchTerm(it) },
        label = {Text("Search player")}
    )
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

@Composable
fun TextRow(
    texts: List<String>,
    modifier: Modifier = Modifier,
    style: TextStyle = Typography.bodyLarge
) {
    Row(modifier = modifier){
        Text(text = texts[0], style = style, modifier = Modifier
            .padding(horizontal = 5.dp)
            .weight(0.75f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis)
        texts.drop(1).forEach { text ->
            Text(text = text, style = style, modifier = Modifier.padding(horizontal = 5.dp))
        }
    }
}

