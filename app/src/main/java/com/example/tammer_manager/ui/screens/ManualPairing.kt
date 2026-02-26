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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.ui.components.NoActiveTournament
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun ManualPairing(
    vmTournament: TournamentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
){
    vmTournament.activeTournament.collectAsState().value?.let { activeTournament ->
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pairingList = vmTournament.currentRoundPairings.collectAsState().value

            if (!pairingList.isEmpty()) {
                LazyColumn(
                    modifier = Modifier.Companion
                        .padding(horizontal = 5.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pairingList.size) { i ->
                        val pairing = pairingList[i]

                        val idWhite = pairing[PlayerColor.WHITE]?.playerID
                        val idBlack = pairing[PlayerColor.BLACK]?.playerID

                        if (idWhite != null && idBlack != null) {
                            ManualPairingItem(
                                vmTournament = vmTournament,
                                pairing = pairing,
                                index = i,
                                setScore = { whitePlayerScore, blackPlayerScore ->
                                    vmTournament.setPairingScore(index = i, playerColor = PlayerColor.WHITE, points = whitePlayerScore)
                                    vmTournament.setPairingScore(index = i, playerColor = PlayerColor.BLACK, points = blackPlayerScore)
                                }
                            )
                        }
                    }
                }
            }

        }
    }?: NoActiveTournament()
}

@Composable
fun PlayerRow(
    vmTournament: TournamentViewModel,
    color: PlayerColor,
    pairingData: HalfPairing?,
    modifier: Modifier = Modifier.Companion,
    borderThickness: Dp = 1.dp,
) {

    val player = vmTournament.findPlayerById(pairingData?.playerID ?: 0)

    val score = player?.score ?: 0f
    val scoreAsText =
        if (score % 1.0 == 0.0) "%,.0f".format(score)
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
            modifier = Modifier.Companion
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
            text = "${player?.fullName ?: '-'} ($scoreAsText)",
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
                //tint = Color.Blue
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
    setScore: (Float, Float) -> Unit
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
                onClick = {  },
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxHeight()
                    .border(
                        width = borderThickness,
                        color = Color.Companion.Black
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