package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.classes.Pairing
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.ui.components.ErrorDialog
import com.example.tammer_manager.ui.components.NoActiveTournament
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun EnterResults(
    vmTournament: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    vmTournament.activeTournament.collectAsState().value?.let {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pairingList = vmTournament.currentRoundPairings.collectAsState().value

            val activeTournament = vmTournament.activeTournament.collectAsState().value
            val completedRounds = activeTournament?.roundsCompleted ?: 0
            val maxRounds = activeTournament?.maxRounds ?: 0

            val (pairingError, setPairingError) = remember { mutableStateOf(false) }
            val (loadingPairs, setLoadingPairs) = remember { mutableStateOf(false) }

            when{ pairingError ->
                ErrorDialog(
                    onDismissRequest = { setPairingError(false) },
                    errorText = "Unable to complete automatic pairing."
                )
            }

            Text(
                text = "Round ${completedRounds + 1} / $maxRounds",
                style = Typography.headlineMedium
            )

            if(loadingPairs){
                Text(
                    text = "Generating pairs...",
                    style = Typography.headlineSmall
                )
            }

            if (!pairingList.isEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pairingList.size) { i ->
                        val pairing = pairingList[i]

                        val idWhite = pairing[PlayerColor.WHITE]?.playerID
                        val idBlack = pairing[PlayerColor.BLACK]?.playerID

                        if(idWhite != null && idBlack != null){
                            PairingItem(
                                vmTournament = vmTournament,
                                pairing = pairing,
                                index = i
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        setLoadingPairs(true)
                        vmTournament.generatePairs(
                        onError = {
                            setLoadingPairs(false)
                            setPairingError(true)
                        },
                        onSuccess = {setLoadingPairs(false)}
                    ) },
                    enabled = pairingList.isEmpty() && !loadingPairs
                ) { Text("Generate pairs") }

                Button(
                    enabled =
                        pairingList.all { pairing ->
                            pairing.all {
                                it.value.points != null
                            }
                        }
                                && !pairingList.isEmpty(),
                    onClick = { vmTournament.finishRound() }
                ) { Text("Finish round") }
            }

            Button(
                onClick = {vmTournament.clearPairings()},
                enabled = !pairingList.isEmpty()
            ){
                Text("Clear pairs")
            }
        }
    }?: NoActiveTournament()
}

@Composable
fun PairingItem(
    index: Int,
    vmTournament: TournamentViewModel,
    pairing: Pairing,
    modifier: Modifier = Modifier,
    borderThickness: Dp = 1.dp
) {
    val (isMenuOpen, setIsMenuOpen) = remember { mutableStateOf(false) }

    val setScore: (Float, Float) -> Unit = { whitePlayerScore, blackPlayerScore ->
        vmTournament.setPairingScore(index = index, playerColor = PlayerColor.WHITE, points = whitePlayerScore)
        vmTournament.setPairingScore(index = index, playerColor = PlayerColor.BLACK, points = blackPlayerScore)
    }
    
    Row(
        modifier = Modifier.height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(borderThickness * -1))
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(borderThickness * -1),
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            PlayerColor.entries.sortedBy { it.ordinal }.forEach {
                PlayerScoreRow (
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    color = it,
                    pairingData = pairing[it],
                    vmTournament = vmTournament)
            }
        }

        Column() {
            IconButton(
                onClick = { setIsMenuOpen(!isMenuOpen) },
                modifier = Modifier
                    .background(Color.Blue)
                    .fillMaxHeight()
                    .border(
                        width = borderThickness,
                        color = Color.Black
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Enter result for pair ${index + 1}",
                    tint=Color.White
                )
            }
            ScoringMenu(isOpen = isMenuOpen, setIsOpen = setIsMenuOpen, modifier = Modifier, setScore = setScore)
        }
    }
}

@Composable
fun PlayerScoreRow(
    vmTournament: TournamentViewModel,
    color: PlayerColor,
    pairingData: HalfPairing?,
    modifier: Modifier = Modifier,
    borderThickness: Dp = 1.dp,
) {

    val player = vmTournament.findPlayerById(pairingData?.playerID ?: 0)

    val score = player?.score ?: 0f
    val scoreAsText =
        if (score % 1.0 == 0.0) "%,.0f".format(score)
        else "%,.1f".format(score)

    Row(modifier = modifier
        .border(
            width = borderThickness,
            color = Color.Black
        )
        .background(color = Color.White)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(borderThickness * -1),
        verticalAlignment = Alignment.CenterVertically
    ){

        Box(modifier = Modifier
            .background(color = color.colorValue)
            .border(width = borderThickness, color = Color.Black)
            .fillMaxHeight()
            .layout() { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val currentHeight = placeable.height

                layout(currentHeight, currentHeight) {
                    placeable.placeRelative(0, 0)
                }
            }) {}

        Text(
            text = "${ player?.fullName ?: '-' } ($scoreAsText)",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .weight(1f),
            style = Typography.bodyLarge
        )

        Text(
            text = when(pairingData?.points){
                null -> "-"
                0.5f -> "½"
                else -> "%,.0f".format(locale = null, pairingData.points)
            },
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 2.dp),
            style = Typography.headlineSmall
        )
    }
}

@Composable
fun ScoringMenu(
    setScore: (Float, Float) -> Unit,
    isOpen: Boolean,
    setIsOpen: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = isOpen,
        onDismissRequest = {setIsOpen(false)},
    ) {
        listOf(
            Pair(1f, 0f),
            Pair(.5f, .5f),
            Pair(0f, 1f),
        ).forEach {
            ScoringMenuItem(it.first,it.second, setScore = setScore, setIsOpen = setIsOpen)
            HorizontalDivider()
        }
        Spacer(Modifier.height(20.dp))
        HorizontalDivider()
        listOf(
            Pair(0f, 0f),
            Pair(.5f, 0f),
            Pair(0f, .5f),
        ).forEach {
            ScoringMenuItem(it.first,it.second, setScore = setScore, setIsOpen = setIsOpen)
            HorizontalDivider()
        }
    }
}

@Composable
fun ScoringMenuItem(
    pointsWhite: Float,
    pointsBlack: Float,
    setScore: (Float, Float) -> Unit,
    setIsOpen: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val whitePointsString = if (pointsWhite == 0.5f) "½" else "%,.0f".format(locale = null, pointsWhite)
    val blackPointsString = if (pointsBlack == 0.5f) "½" else "%,.0f".format(locale = null, pointsBlack)

    DropdownMenuItem(
        text = {
            Text(text = "$whitePointsString - $blackPointsString",
                style = Typography.bodyLarge
            )
        },
        onClick = {
            setScore(pointsWhite, pointsBlack)
            setIsOpen(false)
        }
    )
}