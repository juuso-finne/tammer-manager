package com.example.tammer_manager.viewmodels.tournamentVM

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tammer_manager.data.export_import.ImportedPlayer
import com.example.tammer_manager.data.tournament_admin.classes.PairingList
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.data.tournament_admin.enums.TieBreak
import com.example.tammer_manager.data.tournament_admin.enums.TournamentType
import com.example.tammer_manager.viewmodels.TournamentVMState
import kotlinx.coroutines.flow.StateFlow

class TournamentViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    val activeTournament: StateFlow<Tournament?> = savedStateHandle.getStateFlow(
        key = "tournament",
        initialValue =  null
    )
    val registeredPlayers: StateFlow<List<RegisteredPlayer>> = savedStateHandle.getStateFlow(
        key = "registeredPlayers",
        initialValue = listOf()
    )

    val currentRoundPairings: StateFlow<PairingList> = savedStateHandle.getStateFlow(
        key = "currentRoundPairings",
        initialValue = listOf()
    )

    val filename: StateFlow<String> = savedStateHandle.getStateFlow(
        key = "filename",
        initialValue = ""
    )

    val isGrouped: StateFlow<Boolean> = savedStateHandle.getStateFlow(
        key = "isGrouped",
        initialValue = false
    )

    val currentGroup: StateFlow<String> = savedStateHandle.getStateFlow(
        key = "currentGroup",
        initialValue = ""
    )

    val groupMap: StateFlow<Map<String, TournamentVMState>> = savedStateHandle.getStateFlow(
        key = "groupMap",
        initialValue = mapOf()
    )

    private val vmStateHandler = VMStateHandler(savedStateHandle)
    private val playerStateHandler = PlayerStateHandler(savedStateHandle)
    private val tournamentStateHandler = TournamentStateHandler(
        savedStateHandle = savedStateHandle,
        viewModelScope = viewModelScope,
        playerStateHandler = playerStateHandler,
        vmStateHandler = vmStateHandler
    )
    private val fileHandler = FileHandler(
        savedStateHandle = savedStateHandle,
        vmStateHandler = vmStateHandler
    )

    fun initateTournament(
        name: String,
        maxRounds: Int,
        type: TournamentType,
        tieBreaks: List<TieBreak>
    ){
        tournamentStateHandler.initateTournament(
            name = name,
            maxRounds = maxRounds,
            type = type,
            tieBreaks = tieBreaks
        )
    }

    fun splitTournament(
        updatedPlayerList: List<RegisteredPlayer>
    ){ tournamentStateHandler.splitTournament(updatedPlayerList) }

    fun switchGroup(
        newGroup: String
    ){ tournamentStateHandler.switchGroup(newGroup) }


    fun finishRound(){ tournamentStateHandler.finishRound() }

    fun editRound(round: Int, newResults: PairingList){
        tournamentStateHandler.editRound(round, newResults)
    }

    fun alteringPlayerCountAllowed(): Boolean{
        return(
            activeTournament.value?.type == TournamentType.SWISS ||
            (
                activeTournament.value?.roundsCompleted == 0 &&
                currentRoundPairings.value.isEmpty()
            )
        )
    }

    fun addPlayer(player: ImportedPlayer){ tournamentStateHandler.addPlayer(player) }

    fun removePlayer(index: Int){ tournamentStateHandler.removePlayer(index) }

    fun activatePlayer(index: Int){
        val newList = registeredPlayers.value.toMutableList()
        newList[index] = newList[index].copy(isActive = true)
        savedStateHandle["registeredPlayers"] = newList.toList()
    }

    fun findPlayerById(id: Int): RegisteredPlayer?{
        return registeredPlayers.value.find{it.id == id}
    }

    fun clearPairings(){ tournamentStateHandler.clearPairings() }

    fun setPairs(newPairs: PairingList){ tournamentStateHandler.setPairs(newPairs) }

    fun generatePairs(
        onError: () -> Unit,
        onSuccess: () -> Unit
    ){
        tournamentStateHandler.generatePairs(
            onError = onError,
            onSuccess = onSuccess
        )
    }

    fun setPairingScore(index: Int, playerColor: PlayerColor, points: Float){
        tournamentStateHandler.setPairingScore(
            index = index,
            playerColor = playerColor,
            points = points
        )
    }

    fun save(context: Context): Boolean{ return fileHandler.save(context) }

    fun saveAs(
        newFilename: String,
        context: Context,
        onError: () -> Unit,
        onSuccess: () -> Unit,
        overWrite: Boolean = false,
        confirmOverWrite: () -> Unit = {}
    ){
        fileHandler.saveAs(
            newFilename = newFilename,
            context = context,
            onError = onError,
            onSuccess = onSuccess,
            overWrite = overWrite,
            confirmOverWrite = confirmOverWrite
        )
    }

    fun load (
        context: Context,
        filename: String
    ): Boolean{
        return fileHandler.load(
            context = context,
            filename = filename
        )
    }

    fun delete(
        context: Context,
        filename: String,
    ): Boolean{
        return fileHandler.delete(
            context = context,
            filename = filename
        )
    }

    fun getFileList(context: Context): List<String>{ return fileHandler.getFileList(context) }

    fun exportResults(
        context: Context,
        uri: Uri?,
        onError: () -> Unit,
    ){
        fileHandler.exportResults(
            context = context,
            uri = uri,
            onError = onError
        )
    }
}