package com.example.tammer_manager.ui.screens.player_entry


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.PlayerPoolViewModel
import com.example.tammer_manager.viewmodels.TournamentViewModel

enum class SelectedTab (){
    ENTER_PLAYERS,
    VIEW_PLAYERS
}

@Composable
fun EnterPlayers(
    vmPlayerPool: PlayerPoolViewModel,
    vmTournament: TournamentViewModel,
    modifier: Modifier = Modifier
) {

    val (selectedTab, setSelectedTab) = remember { mutableStateOf(SelectedTab.ENTER_PLAYERS) }
    Column(
        modifier = Modifier.padding(horizontal = 5.dp)
    ){
        TabRow(selectedTab = selectedTab, setSelectedTab = setSelectedTab)

        if(selectedTab == SelectedTab.ENTER_PLAYERS){
            PlayerPoolContainer(
                vmPlayerPool = vmPlayerPool,
                vmTournament = vmTournament,
                modifier = Modifier.weight(1f)
            )
        } else{
            RegisteredPlayerContainer(
                vmTournament = vmTournament,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabRow(selectedTab: SelectedTab, setSelectedTab: (SelectedTab) -> Unit, modifier: Modifier = Modifier) {
    PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {
        Tab(selected = selectedTab.ordinal == SelectedTab.ENTER_PLAYERS.ordinal,
            onClick = { setSelectedTab(SelectedTab.ENTER_PLAYERS) },
            text = {Text("Enter players")}
        )

        Tab(selected = selectedTab == SelectedTab.VIEW_PLAYERS,
            onClick = { setSelectedTab(SelectedTab.VIEW_PLAYERS) },
            text = {Text("View/remove players")}
        )
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