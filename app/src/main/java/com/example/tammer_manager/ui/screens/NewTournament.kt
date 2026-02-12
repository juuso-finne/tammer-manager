package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.data.tournament_admin.enums.TournamentType
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel
import org.apache.commons.lang3.math.NumberUtils

@Composable
fun NewTorunament(
    vmTournament: TournamentViewModel,
    navController: NavController
){
    val (tournamentName, setTournamentName) = remember { mutableStateOf("") }
    val (rounds, setRounds) = remember { mutableIntStateOf(0) }
    val (type, setType) = remember {mutableStateOf(TournamentType.SWISS)}
    val (doubleRoundRobin, setDoubleRoundRobin) = remember { mutableStateOf(false) }

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

        RadioGroupType(
            modifier = Modifier.padding(horizontal = 10.dp),
            selectedType = type,
            setType = setType
        )

        if(type == TournamentType.SWISS){
            TextField(
                label = { Text("Rounds") },
                value = if (rounds == 0) "" else rounds.toString(),
                onValueChange = { setRounds(NumberUtils.toInt(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        } else {
            RadioGroupDouble(
                modifier = Modifier.padding(horizontal = 10.dp),
                doubleroundRobin = doubleRoundRobin,
                setdoubleRoundRobin = setDoubleRoundRobin
            )
        }


        Button(
            onClick = {
                vmTournament.initateTournament(
                    name =  tournamentName,
                    maxRounds = rounds,
                    type = type,
                    doubleRoundRobin = doubleRoundRobin
                )
                navController.navigate("Home")
            },
            enabled =
                (rounds > 0 || type == TournamentType.ROUND_ROBIN) &&
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

@Composable
fun RadioGroupType(
    modifier: Modifier = Modifier,
    selectedType: TournamentType,
    setType: (TournamentType) -> Unit
    ){
    Row (modifier = modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        TournamentType.entries.forEach {
            Row(
                Modifier
                    .selectable(
                        selected = (it == selectedType),
                        onClick = { setType(it) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (it == selectedType),
                    onClick = null
                )
                Text(
                    text = it.toString(),
                    style = Typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun RadioGroupDouble(
    modifier: Modifier = Modifier,
    doubleroundRobin: Boolean,
    setdoubleRoundRobin: (Boolean) -> Unit
){
    Row (modifier = modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
            Row(
                Modifier
                    .selectable(
                        selected = (!doubleroundRobin),
                        onClick = { setdoubleRoundRobin(false) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (!doubleroundRobin),
                    onClick = null
                )
                Text(
                    text = "Single",
                    style = Typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

        Row(
            Modifier
                .selectable(
                    selected = (doubleroundRobin),
                    onClick = { setdoubleRoundRobin(true) },
                    role = Role.RadioButton
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = (doubleroundRobin),
                onClick = null
            )
            Text(
                text = "Double",
                style = Typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}