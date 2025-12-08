package com.example.tammer_manager.ui.screens.enter_players

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

enum class SelectedTab (){
    ENTER_FROM_LIST,
    MANUAL_ENTRY,
    VIEW_PLAYERS,

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabRow(
    vmTournament: TournamentViewModel,
    selectedTab: SelectedTab,
    setSelectedTab: (SelectedTab) -> Unit,
    modifier: Modifier = Modifier) {

    val numberOfRegisteredPlayers = vmTournament.registeredPlayers.collectAsState().value.size
    PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {

        Tab(selected = selectedTab.ordinal == SelectedTab.ENTER_FROM_LIST.ordinal,
            onClick = { setSelectedTab(SelectedTab.ENTER_FROM_LIST) },
            text = {Text("Enter from list")}
        )

        Tab(selected = selectedTab == SelectedTab.MANUAL_ENTRY,
            onClick = { setSelectedTab(SelectedTab.MANUAL_ENTRY) },
            text = { Text("Enter manually") }
            )

        Tab(selected = selectedTab == SelectedTab.VIEW_PLAYERS,
            onClick = { setSelectedTab(SelectedTab.VIEW_PLAYERS) },
            text = {Text("View/remove players ($numberOfRegisteredPlayers)")}
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