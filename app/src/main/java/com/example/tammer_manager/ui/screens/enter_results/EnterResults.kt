package com.example.tammer_manager.ui.screens.enter_results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.ui.components.ErrorDialog
import com.example.tammer_manager.ui.components.NoActiveTournament
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun EnterResults(
    vmTournament: TournamentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier.Companion
) {
    vmTournament.activeTournament.collectAsState().value?.let {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.Companion.fillMaxSize(),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            val pairingList = vmTournament.currentRoundPairings.collectAsState().value

            val activeTournament = vmTournament.activeTournament.collectAsState().value
            val completedRounds = activeTournament?.roundsCompleted ?: 0
            val maxRounds = activeTournament?.maxRounds ?: 0

            val (pairingError, setPairingError) = remember { mutableStateOf(false) }
            val (loadingPairs, setLoadingPairs) = remember { mutableStateOf(false) }

            when {
                pairingError ->
                    ErrorDialog(
                        onDismissRequest = { setPairingError(false) },
                        errorText = "Unable to complete automatic pairing."
                    )
            }

            if (completedRounds < maxRounds){
                HeaderRow(
                    current = completedRounds + 1,
                    max = maxRounds,
                    showLeftArrow = completedRounds > 0,
                    showRightArrow = false,
                    onLeftArrowClick = {},
                    onRightArrowClick = {}
                )
            } else{
                Text(
                    text ="Tournament finished",
                    style = Typography.headlineMedium
                )
            }

            if (loadingPairs) {
                Text(
                    text = "Generating pairs...",
                    style = Typography.headlineSmall
                )
            }

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
                            PairingItem(
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

            if (completedRounds >= maxRounds) {
                Button(
                    onClick = { navController.navigate("standings") }
                ) {
                    Text("View results")
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.Companion.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            setLoadingPairs(true)
                            vmTournament.generatePairs(
                                onError = {
                                    setLoadingPairs(false)
                                    setPairingError(true)
                                },
                                onSuccess = { setLoadingPairs(false) }
                            )
                        },
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
                    onClick = { vmTournament.clearPairings() },
                    enabled = !pairingList.isEmpty()
                ) {
                    Text("Clear pairs")
                }
            }
        }
    }?: NoActiveTournament()
}