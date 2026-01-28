package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.ui.components.NoActiveTournament
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun Standings(
    vmTournament: TournamentViewModel,
    modifier: Modifier = Modifier
){
    vmTournament.activeTournament.collectAsState().value?.let {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val playersState = vmTournament.registeredPlayers.collectAsState().value
            val players = remember(playersState) {
                playersState.sortedByDescending { it.score }
            }

            val activeTournament = vmTournament.activeTournament.collectAsState().value
            val completedRounds = activeTournament?.roundsCompleted ?: 0
            val maxRounds = activeTournament?.maxRounds ?: 0

            Text(
                text =
                    if (completedRounds >= maxRounds) "Final standings"
                    else if (completedRounds != 0) "Standings after round ${completedRounds}"
                    else "Standings",
                style = Typography.headlineMedium
            )

            LazyColumn(modifier = Modifier
                .padding(horizontal = 5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ){
              items(players.size){ i->
                  StandingsItem(players[i])
              }
            }
        }
    }?: NoActiveTournament()
}

@Composable
fun StandingsItem(
    player: RegisteredPlayer
){
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = player.fullName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .padding(horizontal = 2.dp)
                        .weight(1f)
                ,
                style = Typography.bodyLarge
            )

            val scoreAsText =
                if (player.score % 1.0 == 0.0) "%,.0f".format(player.score)
                else "%,.1f".format(player.score)

            Text(
                text = scoreAsText,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 2.dp),
                style = Typography.headlineSmall
            )
        }

        Spacer(Modifier.size(5.dp))
        HorizontalDivider()
    }
}