package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.R
import com.example.tammer_manager.data.tournament_admin.enums.TieBreak
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
    val selectedTieBreaks = remember { mutableStateListOf<TieBreak>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TextField(
            label = { Text(stringResource(R.string.tournament_name)) },
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
                label = { Text(stringResource(R.string.rounds)) },
                value = if (rounds == 0) "" else rounds.toString(),
                onValueChange = { setRounds(NumberUtils.toInt(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        for(i in selectedTieBreaks.indices){
            TieBreakSelector(
                selectedTieBreaks = selectedTieBreaks,
                index = i
            )
        }

        if(selectedTieBreaks.size < TieBreak.entries.size){
            TieBreakSelector(
                selectedTieBreaks = selectedTieBreaks,
                index = selectedTieBreaks.size
            )
        }



        Button(
            onClick = {
                vmTournament.initateTournament(
                    name =  tournamentName,
                    maxRounds = if (type == TournamentType.SWISS) rounds else 0,
                    type = type,
                    tieBreaks = selectedTieBreaks
                )
                navController.navigate("home")
            },
            enabled =
                (rounds > 0 || type != TournamentType.SWISS) &&
                tournamentName.isNotEmpty()
            ,
            modifier = Modifier.padding(top = 10.dp)
        )
        { Text(stringResource(R.string.create_tournament)) }

        Button(
            onClick = { navController.navigate("home") }
        ){
            Text(stringResource(R.string.cancel))
        }
    }
}

@Composable
fun RadioGroupType(
    modifier: Modifier = Modifier,
    selectedType: TournamentType,
    setType: (TournamentType) -> Unit
    ){
    Row (modifier = modifier
        .fillMaxWidth()
        .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        TournamentType.entries.forEach {
            Column(
                Modifier
                    .selectable(
                        selected = (it == selectedType),
                        onClick = { setType(it) },
                        role = Role.RadioButton
                    ),
            ) {
                RadioButton(
                    selected = (it == selectedType),
                    onClick = null
                )
                Text(
                    text = stringResource(it.label),
                    style = Typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
fun TieBreakSelector(
    modifier: Modifier = Modifier,
    selectedTieBreaks: SnapshotStateList<TieBreak>,
    index: Int
){
    val availableTieBreaks = TieBreak.entries - selectedTieBreaks
    val (isOpen, setIsOpen) = remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){

        Text(
            text = stringResource(R.string.tie_break_x, index + 1),
            style = Typography.bodyLarge
        )

        Spacer(modifier = Modifier.width(10.dp))

        Row(modifier = modifier
            .border(
                width = 1.dp,
                color = Color.Black
            )
            .padding(5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){

            Text(
                style = Typography.bodyLarge,
                text =
                    if(selectedTieBreaks.size > index) selectedTieBreaks[index].toString()
                    else stringResource(R.string.select)
            )

            IconButton(
                onClick = {
                    setIsOpen(true)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.choose_tie_break),
                    tint = Color.Blue
                )
            }
        }

        DropdownMenu(
            expanded = isOpen,
            onDismissRequest = { setIsOpen(false) },
        ){
            if(selectedTieBreaks.size > index){
                DropdownMenuItem(
                    text = { Text (stringResource(R.string.remove)) },
                    onClick = {
                        selectedTieBreaks.removeAt(index)
                        setIsOpen(false)
                    }
                )
            }
           availableTieBreaks.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.toString()) },
                    onClick = {
                        if(selectedTieBreaks.size > index){
                            selectedTieBreaks[index] = it
                        } else {
                            selectedTieBreaks.add(it)
                        }
                        setIsOpen(false)
                    }
                )
            }
        }
    }
}