package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.tammer_manager.data.player_import.ImportedPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import com.example.tammer_manager.data.tournament_admin.enums.TournamentType
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun Home(
    vmTournament: TournamentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val tournament = vmTournament.activeTournament.collectAsState().value
        val context = LocalContext.current

        Text(
            text = if(tournament != null) "${tournament.name}:" else "No active tournament",
            style = Typography.bodyLarge
        )

        Text(
            text =
                if (tournament != null && tournament.roundsCompleted < tournament.maxRounds)
                    "${tournament.roundsCompleted}/${tournament.maxRounds} rounds played"
                else if(tournament != null && tournament.maxRounds != 0) "Tournament complete"
                else ""
            ,
            style = Typography.bodyLarge
        )

        Spacer(Modifier.height(20.dp))

        Button (
            onClick = { navController.navigate("newTournament") }
        ){
            Text("New tournament")
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ){
            Button (
                onClick = { navController.navigate("saveTournament") },
                enabled = tournament != null
            ){
                Text("Save as...")
            }

            Button (
                onClick = { vmTournament.save(context) },
                enabled = tournament != null && vmTournament.filename.collectAsState().value.isNotEmpty()
            ){
                Text("Save")
            }

            Button (
                onClick = { navController.navigate("fileBrowser") },
            ){
                Text("Load")
            }
        }


        Button(
            onClick = {
                vmTournament.initateTournament(name = "Placeholder", maxRounds = 5, TournamentType.SWISS)
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
            Text("Create placeholder tournament of 11 [DEBUG/DEV]")
        }

        Button(
            onClick = {
                vmTournament.initateTournament(name = "Placeholder", maxRounds = 5, TournamentType.SWISS)
                val players = mutableListOf<ImportedPlayer>()
                for(i in 0 until 50){
                    vmTournament.addPlayer(ImportedPlayer("Player $i", 1000 + i*20))
                }
            }
        ) {
            Text("Create placeholder tournament of 50 [DEBUG/DEV]")
        }
    }
}