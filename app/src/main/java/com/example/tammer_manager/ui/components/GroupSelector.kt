package com.example.tammer_manager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.tammer_manager.R
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun GroupSelector(
    modifier: Modifier = Modifier,
    vmTournament: TournamentViewModel
){
    val (isOpen, setIsOpen) = remember { mutableStateOf(false) }
    val groups = vmTournament.groupMap.collectAsState().value.keys.sorted()
    val currentGroup = vmTournament.currentGroup.collectAsState().value
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = stringResource(R.string.group_x, currentGroup),
            style = Typography.bodyLarge
        )

        IconButton(
            onClick = {
                setIsOpen(true)
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.choose_group),
                tint = Color.Blue
            )
        }
        DropdownMenu(
            expanded = isOpen,
            onDismissRequest = { setIsOpen(false) },
        ){
            groups.minus(setOf(currentGroup)).forEach {
                DropdownMenuItem(
                    modifier = modifier,
                    text = { Text(text = stringResource(R.string.group_x, it)) },
                    onClick = {
                        vmTournament.switchGroup(it)
                        setIsOpen(false)
                    }
                )
            }
        }
    }
}