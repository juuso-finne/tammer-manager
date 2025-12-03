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
import com.example.tammer_manager.data.interfaces.Player
import com.example.tammer_manager.data.player_import.ImportedPlayer
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.PlayerPoolViewModel

@Composable
fun EnterPlayers(
    vmPlayerPool: PlayerPoolViewModel,
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
                }
            )
            SearchBar(
                searchTerm = searchTerm,
                setSearchTerm = setSearchTerm
            )
        }

        HorizontalDivider(thickness = 2.dp)


        Column(modifier = Modifier.weight(1f)){
        }
    }

}

@Composable
fun PlayerPool(
    players: List<ImportedPlayer>,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        LazyColumn() {
            items(players.size) { i ->
                PlayerPoolItem(players[i])
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
    modifier: Modifier = Modifier
) {
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
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Add,
                    tint = Color.Blue,
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
fun TournamentHeader(modifier: Modifier = Modifier) {
    
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

