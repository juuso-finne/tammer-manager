package com.example.tammer_manager.ui.screens.enter_players

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.ui.screens.enter_players.tabs.ManualEntry
import com.example.tammer_manager.ui.screens.enter_players.tabs.PlayerPoolContainer
import com.example.tammer_manager.ui.screens.enter_players.tabs.RegisteredPlayerContainer
import com.example.tammer_manager.viewmodels.PlayerPoolViewModel
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun EnterPlayers(
    vmPlayerPool: PlayerPoolViewModel,
    vmTournament: TournamentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val (selectedTab, setSelectedTab) = remember { mutableStateOf(SelectedTab.ENTER_FROM_LIST) }
    Column(
        modifier = Modifier.padding(horizontal = 5.dp)
    ){
        TabRow(
            selectedTab = selectedTab,
            setSelectedTab = setSelectedTab,
            vmTournament = vmTournament
        )

        when (selectedTab) {
            SelectedTab.ENTER_FROM_LIST -> {
                PlayerPoolContainer(
                    vmPlayerPool = vmPlayerPool,
                    vmTournament = vmTournament,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }
            SelectedTab.VIEW_PLAYERS -> {
                RegisteredPlayerContainer(
                    vmTournament = vmTournament,
                    modifier = Modifier.weight(1f)
                )
            }

            SelectedTab.MANUAL_ENTRY -> {
                ManualEntry(vmTournament = vmTournament)
            }
        }
    }
}