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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.R
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.ui.components.ConfirmDialog
import com.example.tammer_manager.ui.components.ErrorDialog
import com.example.tammer_manager.ui.components.GroupSelector
import com.example.tammer_manager.ui.components.NoActiveTournament
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun EnterResults(
    vmTournament: TournamentViewModel,
    navController: NavController
) {
    vmTournament.activeTournament.collectAsState().value?.let { activeTournament ->
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pairingList = vmTournament.currentRoundPairings.collectAsState().value
            val playerCount = vmTournament.registeredPlayers.collectAsState().value.size

            val completedRounds = activeTournament.roundsCompleted
            val maxRounds = activeTournament.maxRounds

            val (pairingError, setPairingError) = remember { mutableStateOf(false) }
            val (loadingPairs, setLoadingPairs) = remember { mutableStateOf(false) }
            val (clearPairsDialog, setClearPairsDialog) = remember { mutableStateOf(false) }

            val isGrouped = vmTournament.isGrouped.collectAsState().value

            when {
                pairingError ->
                    ErrorDialog(
                        onDismissRequest = { setPairingError(false) },
                        errorText = stringResource(R.string.error_auto_pairing)
                    )
            }

            if(isGrouped){
                GroupSelector(vmTournament = vmTournament)
            }

            if(playerCount <= 1){
                Text(
                    text = stringResource(R.string.need_more_players),
                    style = Typography.headlineMedium
                )
            } else if (completedRounds < maxRounds){
                HeaderRow(
                    current = completedRounds + 1,
                    max = maxRounds,
                    showLeftArrow = completedRounds > 0,
                    showRightArrow = false,
                    onLeftArrowClick = { navController.navigate("editResults") },
                    onRightArrowClick = {}
                )
            } else{
                Text(
                    text = stringResource(R.string.tournament_finished),
                    style = Typography.headlineMedium
                )
            }

            if (loadingPairs) {
                Text(
                    text = stringResource(R.string.generating_pairs),
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
                    Text(stringResource(R.string.view_results))
                }

                Button(
                    onClick = { navController.navigate("editResults") }
                ) {
                    Text(stringResource(R.string.edit_results))
                }
            } else {
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
                                onSuccess = { setLoadingPairs(false) }
                            )
                        },
                        enabled =
                            pairingList.isEmpty() &&
                            !loadingPairs &&
                            playerCount > 1
                    ) { Text(stringResource(R.string.generate_pairs)) }

                    Button(
                        enabled =
                            pairingList.all { pairing ->
                                pairing.all {
                                    it.value.points != null
                                }
                            }
                            && !pairingList.isEmpty(),
                        onClick = { vmTournament.finishRound() }
                    ) { Text(stringResource(R.string.finish_round)) }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Button(
                        onClick = { navController.navigate("manualPairing") }
                    ){ Text(stringResource(R.string.manual_pairing)) }

                    Button(
                        onClick = { setClearPairsDialog(true) },
                        enabled = !pairingList.isEmpty()
                    ) {
                        Text(stringResource(R.string.clear_pairs))
                    }
                }
            }

            when { clearPairsDialog ->
                ConfirmDialog(
                    onDismissRequest = { setClearPairsDialog(false) },
                    onConfirmRequest = {
                        vmTournament.clearPairings()
                        setClearPairsDialog(false)
                    },
                    confirmButtonText = stringResource(R.string.yes),
                    dismissButtonText = stringResource(R.string.no),
                    dialogText = stringResource(R.string.confirm_clear_all)
                )
            }
        }
    }?: NoActiveTournament()
}