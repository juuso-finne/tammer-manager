package com.example.tammer_manager.ui.screens

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun ManualPairing(
    vmTournament: TournamentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
){
    val currentRoundPairings by vmTournament.currentRoundPairings.collectAsState()
    val players = vmTournament.registeredPlayers.collectAsState().value.filter{it.isActive}
    val globalPairs = remember(currentRoundPairings) {
        currentRoundPairings.filter { it[PlayerColor.BLACK]?.playerID != null }
    }
    val localPairs = remember(globalPairs) {
        mutableStateListOf<Pairing>().apply { addAll(globalPairs) }
    }
    val unsavedChanges by remember (globalPairs, localPairs) { derivedStateOf{ globalPairs != localPairs }  }

    val unpairedPlayers = remember (localPairs) { derivedStateOf {
        players.minus(localPairs
            .flatMap { pair -> pair.values.filter { it.playerID != null }
            .map { vmTournament.findPlayerById(it.playerID!!)!! } }
            .toSet()
        )
     } }

    val remainingPairs = players.size/2 - localPairs.size

    fun setPlayer(
        pairIndex: Int,
        color: PlayerColor,
        playerID: Int?
    ){
        val copy = localPairs[pairIndex].toMutableMap()
        copy[color] = HalfPairing(playerID = playerID)
        localPairs[pairIndex] = copy
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()){
            val addButtonEnabled = remainingPairs > 0
            IconButton(
                onClick = {localPairs.add(0,mapOf())},
                enabled = addButtonEnabled
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new pair",
                    tint = if (addButtonEnabled) Color.Blue else Color.DarkGray,
                )
            }

            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "Pairs remaining: $remainingPairs",
                style = Typography.labelMedium
            )
        }

        if (!localPairs.isEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(localPairs.size) { i ->
                val pairing = localPairs[i]
                    ManualPairingItem(
                        vmTournament = vmTournament,
                        pairing = pairing,
                        selectPlayer = { color, playerId ->  setPlayer(i, color, playerId) },
                        deletePair = { localPairs.removeAt(i) },
                        players = unpairedPlayers.value
                    )
                }
            }
        }

        Button(onClick = {localPairs.clear()}){
            Text("Clear all")
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                enabled =
                    unpairedPlayers.value.size < 2 &&
                    unsavedChanges
                ,
                onClick = {
                    val localPairsCopy = localPairs.toMutableList()
                    if (unpairedPlayers.value.size == 1){
                        val playerReceivingBye = unpairedPlayers.value[0]
                        localPairsCopy.add(mapOf(
                            PlayerColor.WHITE to HalfPairing(playerReceivingBye.id, 1f),
                            PlayerColor.BLACK to HalfPairing(null, 0f)
                        ))
                    }
                    vmTournament.setPairs(localPairsCopy)
                }
            ) {
                Text("Save changes")
            }

            Button(
                enabled = unsavedChanges,
                onClick = {
                    localPairs.clear()
                    localPairs.addAll(globalPairs)
                }
            ){
                Text("Reset changes")
            }
        }

        Button(onClick = { navController.navigate("enterResults") }){
            Text ("Cancel")
        }
    }
}

@Composable
fun PlayerRow(
    vmTournament: TournamentViewModel,
    color: PlayerColor,
    pairingData: HalfPairing?,
    modifier: Modifier = Modifier,
    borderThickness: Dp = 1.dp,
    selectPlayer: (playerID: Int?) -> Unit,
    players: List<RegisteredPlayer>
) {

    val player = pairingData?.playerID?.let{vmTournament.findPlayerById(it)}

    val score = player?.score
    val scoreAsText =
        if ((score ?: 0f) % 1.0 == 0.0) "%,.0f".format(score)
        else "%,.1f".format(score)

    val (dropDownMenuOpen, setDropDownMenuOpen) = remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .border(
                width = borderThickness,
                color = Color.Black
            )
            .background(color = Color.White)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(borderThickness * -1),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .background(color = color.colorValue)
                .border(width = borderThickness, color = Color.Black)
                .fillMaxHeight()
                .layout() { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val currentHeight = placeable.height

                    layout(currentHeight, currentHeight) {
                        placeable.placeRelative(0, 0)
                    }
                }
        ) {}

        Text(
            text =
                if (player == null) ""
                else "${player.fullName}, ${player.rating} ($scoreAsText)",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .weight(1f),
            style = Typography.bodyLarge
        )

        IconButton(
            onClick = {
                setDropDownMenuOpen(true)
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Choose player",
                tint = Color.Blue
            )
        }

        PlayerDropdownMenu(
            players = players,
            selectPlayer = selectPlayer,
            isOpen = dropDownMenuOpen,
            setIsOpen = setDropDownMenuOpen
        )
    }
}

@Composable
fun ManualPairingItem(
    vmTournament: TournamentViewModel,
    pairing: Pairing,
    modifier: Modifier = Modifier,
    borderThickness: Dp = 1.dp,
    selectPlayer: (
        color: PlayerColor,
        playerID: Int?
    ) -> Unit,
    deletePair: () -> Unit,
    players: List<RegisteredPlayer>
){
    Row(
        modifier = Modifier.height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(borderThickness * -1)
    )
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(borderThickness * -1),
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            PlayerColor.entries.sortedBy { it.ordinal }.forEach {
                PlayerRow(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    color = it,
                    pairingData = pairing[it],
                    vmTournament = vmTournament,
                    selectPlayer = { playerID -> selectPlayer(it, playerID) },
                    players = players
                )
            }
        }

        Column() {
            IconButton(
                onClick = { deletePair() },
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxHeight()
                    .border(
                        width = borderThickness,
                        color = Color.Black
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete pair",
                    tint = Color.Blue
                )
            }
        }
    }
}

@Composable
fun PlayerDropdownMenu(
    players: List<RegisteredPlayer>,
    selectPlayer: (id: Int?) -> Unit,
    isOpen: Boolean,
    setIsOpen: (Boolean) -> Unit,
){
    DropdownMenu(
        expanded = isOpen,
        onDismissRequest = { setIsOpen(false) },
    ){
        PlayerDropdownItem(
            player = null,
            setIsOpen = setIsOpen,
            selectPlayer = selectPlayer
        )
        players.forEach {
            PlayerDropdownItem(
                player = it,
                setIsOpen = setIsOpen,
                selectPlayer = selectPlayer
            )
        }
    }
}

@Composable
fun PlayerDropdownItem(
    player: RegisteredPlayer?,
    setIsOpen: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    selectPlayer: (id: Int?) -> Unit
){
    val score = player?.score
    val scoreAsText =
        if ((score ?: 0f) % 1.0 == 0.0) "%,.0f".format(score)
        else "%,.1f".format(score)

    DropdownMenuItem(
        modifier = modifier,
        text = {
            Text(
                text =
                    if (player == null) "<Empty>"
                    else "${player.fullName}, ${player.rating} ($scoreAsText)",
                style = Typography.bodyLarge
            )
        },
        onClick = {
            selectPlayer(player?.id)
            setIsOpen(false)
        }
    )
}