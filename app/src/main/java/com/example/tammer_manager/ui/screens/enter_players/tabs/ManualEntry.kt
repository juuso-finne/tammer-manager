package com.example.tammer_manager.ui.screens.enter_players.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tammer_manager.R
import com.example.tammer_manager.data.export_import.ImportedPlayer
import com.example.tammer_manager.viewmodels.TournamentViewModel
import kotlinx.coroutines.launch
import org.apache.commons.lang3.math.NumberUtils

@Composable
fun ManualEntry(
    vmTournament: TournamentViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()

    val (givenName, setGivenName) = remember { mutableStateOf("") }
    val (familyName, setFamilyName) = remember { mutableStateOf("") }
    val (rating, setRating) = remember { mutableIntStateOf(1000) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextField(
            label = { Text(stringResource(R.string.family_name)) },
            value = familyName,
            onValueChange = { setFamilyName(it) },
        )

        TextField(
            label = { Text(stringResource(R.string.given_name)) },
            value = givenName,
            onValueChange = { setGivenName(it) },
        )


        TextField(
            label = { Text("Rating") },
            value = rating.toString(),
            onValueChange = { setRating(NumberUtils.toInt(it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        val message = stringResource(R.string.x_added, "$familyName, $givenName")
        Button(
            onClick = {
                val newPlayer = ImportedPlayer(
                    fullName = "$familyName, $givenName",
                    rating = rating
                )

                vmTournament.addPlayer(newPlayer)
                scope.launch { snackbarHostState.showSnackbar(message) }
                setGivenName("")
                setFamilyName("")
            },
            enabled =
                vmTournament.alteringPlayerCountAllowed() &&
                !givenName.isEmpty() &&
                !familyName.isEmpty() &&
                rating > 0 && rating < 5000
            ,
            modifier = Modifier.padding(top = 10.dp)
        )
        { Text(stringResource(R.string.enter_player)) }
    }
}