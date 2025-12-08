package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.data.player_import.ImportedPlayer
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel
import org.apache.commons.lang3.math.NumberUtils.toInt

@Composable
fun ManualEntry(
    navController: NavController,
    vmTournament: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val (givenName, setGivenName) = remember{ mutableStateOf("") }
    val (familyName, setFamilyName) = remember{ mutableStateOf("") }
    val (rating, setRating) = remember { mutableIntStateOf(1000) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Manual entry",
            style = Typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth()
        )

        TextField(
            label = {Text("Family name")},
            placeholder = {Text("Smith")},
            value = familyName,
            onValueChange = { setFamilyName(it) },
        )

        TextField(
            label = {Text("Given name")},
            placeholder = {Text("John")},
            value = givenName,
            onValueChange = { setGivenName(it) },
        )


        TextField(
            label = {Text("Rating")},
            value = rating.toString(),
            onValueChange = { setRating(toInt(it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ){
            Button(
                onClick = { vmTournament.addPlayer(ImportedPlayer(
                    fullName = "$familyName, $givenName",
                    rating = rating
                )) },
                enabled = !givenName.isEmpty() && !familyName.isEmpty())
            { Text("Enter player")}


            Button(onClick = {navController.navigate("enterPlayers")})
            { Text("Return")}
        }
    }
}