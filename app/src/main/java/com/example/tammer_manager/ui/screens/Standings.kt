package com.example.tammer_manager.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tammer_manager.R
import com.example.tammer_manager.data.tournament_admin.classes.MatchHistoryItem
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.enums.TieBreak
import com.example.tammer_manager.ui.components.ErrorDialog
import com.example.tammer_manager.ui.components.GroupSelector
import com.example.tammer_manager.ui.components.NoActiveTournament
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun Standings(
    vmTournament: TournamentViewModel
){
    vmTournament.activeTournament.collectAsState().value?.let {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current
            val (exportError, setExportError) = remember { mutableStateOf(false) }

            val documentPicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument(
                    mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
            ){ uri ->
                vmTournament.exportResults(
                    context = context,
                    uri = uri,
                    onError = { setExportError(true) },
                )
            }

            val activeTournament = vmTournament.activeTournament.collectAsState().value
            val tieBreaks = activeTournament!!.tieBreaks

            val playersState = vmTournament.registeredPlayers.collectAsState().value
            val players = remember(playersState) {
                playersState.sortedWith(
                    compareByDescending<RegisteredPlayer> { it.score }.thenComparator
                    { a, b ->
                        var diff = 0f
                        for (i in tieBreaks.indices) {
                            val tieBreak = tieBreaks[i]
                            diff = tieBreak.calculate(a, playersState) - tieBreak.calculate(
                                b,
                                playersState
                            )
                            if (diff != 0f) {
                                break
                            }
                        }
                        (-2 * diff).toInt()
                    }
                )
            }

            val completedRounds = activeTournament.roundsCompleted
            val maxRounds = activeTournament.maxRounds

            val isGrouped = vmTournament.isGrouped.collectAsState().value

            val (showTieBreaks, setShowTieBreaks) = remember { mutableStateOf(false) }
            val (showRoundResults, setShowRoundResults) = remember { mutableStateOf(false) }

            Text(
                text =
                    if (completedRounds >= maxRounds && maxRounds != 0) stringResource(R.string.final_standings)
                    else if (completedRounds != 0) stringResource(R.string.standings_after_round_x, completedRounds)
                    else stringResource(R.string.standings),
                style = Typography.headlineMedium
            )

            if(isGrouped){
                GroupSelector(vmTournament = vmTournament)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                if(tieBreaks.isNotEmpty()){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Text(
                            style = Typography.labelLarge,
                            text = "Show tie-breaks"
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Switch(
                            checked = showTieBreaks,
                            onCheckedChange = { setShowTieBreaks(!showTieBreaks) },
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Text(
                        style = Typography.labelLarge,
                        text = "Show round results"
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Switch(
                        checked = showRoundResults,
                        onCheckedChange = { setShowRoundResults(!showRoundResults) },
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .weight(1f)
                ,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ){
              items(players.size){ i ->
                  StandingsItem(
                      player = players[i],
                      placement = i + 1,
                      showTieBreaks = showTieBreaks,
                      players = players,
                      tieBreaks = tieBreaks,
                      showRoundResults = showRoundResults
                  )
              }
            }

            Button(onClick = {
                documentPicker.launch(
                    "Tournament" +
                    LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                )
            }) {
                Text (stringResource(R.string.export_as_xlsx))
            }

            when{ exportError ->
                ErrorDialog(
                    onDismissRequest = { setExportError(false) },
                    errorText = stringResource(R.string.error_export)
                )
            }
        }
    }?: NoActiveTournament()
}

@Composable
fun StandingsItem(
    player: RegisteredPlayer,
    placement: Int,
    showTieBreaks: Boolean,
    players: List<RegisteredPlayer>,
    tieBreaks: List<TieBreak>,
    showRoundResults: Boolean
){
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "$placement. ${player.fullName}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .padding(horizontal = 2.dp)
                        .weight(1f)
                ,
                style = Typography.bodyLarge
            )

            val scoreAsText =
                if (player.score % 1.0 == 0.0) "%,.0f".format(player.score)
                else "%,.1f".format(player.score)

            var tieBreakText = ""

            for (i in tieBreaks.indices){
                val tieBreak = tieBreaks[i]
                val value = tieBreak.calculate(player, players)

                tieBreakText += "${tieBreak.abbreviation}:"
                tieBreakText += if (tieBreak == TieBreak.WINS) value.toInt() else value

                if(i < tieBreaks.indices.last){
                    tieBreakText += ", "
                }
            }

            Row(verticalAlignment = Alignment.Bottom){
                Text(
                    text = scoreAsText,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 2.dp),
                    style = Typography.headlineSmall.copy(lineHeight = 20.sp),
                )

                if(showTieBreaks){
                    Text(
                        text = tieBreakText,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 5.dp),
                        style = Typography.bodyMedium
                    )
                }
            }
        }

        if(showRoundResults){ RoundResults(player, players) }

        Spacer(Modifier.size(5.dp))
        HorizontalDivider()
    }
}

@Composable
fun RoundResults(
    player: RegisteredPlayer,
    players: List<RegisteredPlayer>,
){
    val sortedHistory = player.matchHistory.sortedBy { it.round }

    fun getResultText(match: MatchHistoryItem):String{

        if(match.opponentId == null){ return "bye" }

        var resultText = "${players.indexOfFirst { it.id == match.opponentId } + 1}:"

        resultText +=
            if(match.result == .5f) "½"
            else "${match.result.toInt()}"


        return resultText
    }

    LazyRow (modifier = Modifier.fillMaxWidth()){
        items(sortedHistory.size){
            val match = sortedHistory[it]
            Text(
                text = getResultText(match),
                color = match.color.reverse().colorValue,
                modifier = Modifier
                    .background(match.color.colorValue)
                    .border(BorderStroke(1.dp, Color.Black))
                    .padding(2.dp)
            )
        }
    }
}