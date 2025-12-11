package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun Home(
    vmTournament: TournamentViewModel,
    modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                vmTournament.initateTournament(name = "Palceholder", maxRounds = 5)
            }
        ) {
            Text("Create tournament")
        }
    }
}