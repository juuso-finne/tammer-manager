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
import com.example.tammer_manager.data.player_import.ImportedPlayer
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
                vmTournament.initateTournament(name = "Placeholder", maxRounds = 5)
                listOf(
                    ImportedPlayer("Hannu Hanhi", 2000),
                    ImportedPlayer("Aku Ankka", 1900),
                    ImportedPlayer("Sari Shakinpelaaja", 1800),
                    ImportedPlayer("Rymy-Eetu", 1750),
                    ImportedPlayer("Sakke Shakinpelaaja", 1700),
                    ImportedPlayer("Matti Mainio", 1650),
                    ImportedPlayer("Paavo Puuntuuppaaja", 1600),
                    ImportedPlayer("Jussi Juonio", 1550),
                    ImportedPlayer("Kaino Vieno", 1500),
                    ImportedPlayer("Esko Unohtumaton", 1450),
                    ImportedPlayer("Antti Antinpoika", 1400),
                ).forEach { vmTournament.addPlayer(it) }
            }
        ) {
            Text("Create placeholder tournament [DEBUG/DEV]")
        }
    }
}