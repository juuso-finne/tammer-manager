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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.data.tournament_admin.classes.HalfPairing
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.data.tournament_admin.reconstructPairings
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun EditResults(
    vmTournament: TournamentViewModel,
    navController: NavController,
){
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.Companion.fillMaxSize(),
        horizontalAlignment = Alignment.Companion.CenterHorizontally
    ){
        val activeTournament = vmTournament.activeTournament.collectAsState().value
        val players = vmTournament.registeredPlayers.collectAsState().value

        val completedRounds = activeTournament?.roundsCompleted ?: 0
        val maxRounds = activeTournament?.maxRounds ?: 0

        val (currentlyEditingRound, setCurrentlyEditingRound) = remember {
            mutableIntStateOf(completedRounds)
        }

        val reconstructedPairings = remember(players, currentlyEditingRound) {
            reconstructPairings(players = players, round = currentlyEditingRound)
        }

        val (localResults, setLocalResults) = remember (players, currentlyEditingRound) {
            mutableStateOf(reconstructedPairings)
        }

        val unsavedChanges = remember(localResults, reconstructedPairings) {
            localResults != reconstructedPairings
        }

        HeaderRow(
            current = currentlyEditingRound,
            max = maxRounds,
            onLeftArrowClick = { setCurrentlyEditingRound(currentlyEditingRound - 1) },
            onRightArrowClick = {
                if (completedRounds < maxRounds && currentlyEditingRound == completedRounds){
                    navController.navigate("enterResults")
                } else {
                    setCurrentlyEditingRound(currentlyEditingRound + 1)
                }
            }
        )

        if (!localResults.isEmpty()) {
            LazyColumn(
                modifier = Modifier.Companion
                    .padding(horizontal = 5.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(localResults.size) { i ->
                    val pairing = localResults[i]

                    val idWhite = pairing[PlayerColor.WHITE]?.playerID
                    val idBlack = pairing[PlayerColor.BLACK]?.playerID

                    if (idWhite != null && idBlack != null) {
                        PairingItem(
                            vmTournament = vmTournament,
                            pairing = pairing,
                            index = i,
                            setScore = { whitePlayerScore, blackPlayerScore ->
                                val newPairingList = localResults.toMutableList()
                                val newPairing = newPairingList[i].toMutableMap()
                                newPairing[PlayerColor.WHITE] = HalfPairing(idWhite, whitePlayerScore)
                                newPairing[PlayerColor.BLACK] = HalfPairing(idBlack, blackPlayerScore)

                                newPairingList[i] = newPairing
                                setLocalResults(newPairingList)
                            }
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                onClick = {},
                enabled = unsavedChanges
            ){
                Text("Save changes")
            }

            Button(
                onClick = {},
                enabled = unsavedChanges
            ){
                Text("Reset changes")
            }
        }

    }
}