package com.example.tammer_manager.ui.screens

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
    val globalPairs = vmTournament.currentRoundPairings.collectAsState().value
    val localPairs = remember { mutableStateListOf<Pairing>().apply { addAll(globalPairs) } }
    val unsavedChanges = remember { derivedStateOf { globalPairs != localPairs } }

    fun addPair(){
        localPairs.add(
            mapOf()
        )
    }

    fun setPlayer(
        pairIndex: Int,
        color: PlayerColor,
        playerID: Int
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
        if (!localPairs.isEmpty()) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(localPairs.size) { i ->
                val pairing = localPairs[i]

                val idWhite = pairing[PlayerColor.WHITE]?.playerID
                val idBlack = pairing[PlayerColor.BLACK]?.playerID

                if (idWhite != null && idBlack != null) {
                    ManualPairingItem(
                        vmTournament = vmTournament,
                        pairing = pairing,
                        index = i,
                        setPlayer = { color, playerId ->  setPlayer(i, color, playerId) },
                        deletePair = { localPairs.removeAt(i) }
                    )
                }
            }
        }
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
) {

    val player = pairingData?.playerID?.let{vmTournament.findPlayerById(it)}

    val score = player?.score
    val scoreAsText =
        if ((score ?: 0f) % 1.0 == 0.0) "%,.0f".format(score)
        else "%,.1f".format(score)

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
            text = "${player?.fullName}, ${player?.rating} ($scoreAsText)",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .weight(1f),
            style = Typography.bodyLarge
        )

        IconButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Choose player",
                tint = Color.Blue
            )
        }
    }
}

@Composable
fun ManualPairingItem(
    index: Int,
    vmTournament: TournamentViewModel,
    pairing: Pairing,
    modifier: Modifier = Modifier,
    borderThickness: Dp = 1.dp,
    setPlayer: (
        color: PlayerColor,
        playerID: Int
    ) -> Unit,
    deletePair: () -> Unit
){
    val (isMenuOpen, setIsMenuOpen) = remember { mutableStateOf(false) }

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
                    vmTournament = vmTournament
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
fun PlayerDropDownItem(
    player: RegisteredPlayer,
    setIsOpen: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    selectPlayer: (id: Int) -> Unit
){
    val score = player.score
    val scoreAsText =
        if (score % 1.0 == 0.0) "%,.0f".format(score)
        else "%,.1f".format(score)

    DropdownMenuItem(
        modifier = modifier,
        text = {
            Text(
                text = "${player.fullName}, ${player.rating} ($scoreAsText)",
                style = Typography.bodyLarge
            )
        },
        onClick = {
            selectPlayer(player.id)
            setIsOpen(false)
        }
    )
}