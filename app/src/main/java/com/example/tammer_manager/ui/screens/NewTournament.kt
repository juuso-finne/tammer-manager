package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.viewmodels.TournamentViewModel
import org.apache.commons.lang3.math.NumberUtils

@Composable
fun NewTorunament(
    vmTournament: TournamentViewModel,
    navController: NavController
){
    val (tournamentName, setTournamentName) = remember { mutableStateOf("") }
    val (rounds, setRounds) = remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TextField(
            label = { Text("Tournament name") },
            value = tournamentName,
            onValueChange = { setTournamentName(it) },
        )

        TextField(
            label = { Text("Rounds") },
            value = if (rounds == 0) "" else rounds.toString(),
            onValueChange = { setRounds(NumberUtils.toInt(it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                vmTournament.initateTournament(tournamentName, rounds)
                navController.navigate("Home")
            },
            enabled =
                rounds > 0 &&
                tournamentName.isNotEmpty()
            ,
            modifier = Modifier.padding(top = 10.dp)
        )
        { Text("Create tournament") }

        Button(
            onClick = { navController.navigate("Home") }
        ){
            Text("Cancel")
        }
    }
}