package com.example.tammer_manager.viewmodels.tournamentVM

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tammer_manager.data.export_import.ImportedPlayer
import com.example.tammer_manager.data.file_management.deleteTournament
import com.example.tammer_manager.data.file_management.listTournaments
import com.example.tammer_manager.data.file_management.loadTournament
import com.example.tammer_manager.data.file_management.saveGroup
import com.example.tammer_manager.data.file_management.saveTournament
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

    val nextPlayerId: StateFlow<Int> = savedStateHandle.getStateFlow(
        key = "nextPlayerId",
        initialValue = 0
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
    fun setVMState(data: TournamentVMState){
        savedStateHandle["tournament"] = data.tournament
        savedStateHandle["registeredPlayers"] = data.registeredPlayers
        savedStateHandle["nextPlayerId"] = data.nextPlayerId
        savedStateHandle["currentRoundPairings"] = data.currentRoundPairings
        savedStateHandle["isGrouped"] = data.isGrouped
        savedStateHandle["currentGroup"] = data.currentGroup
    }

    fun getVMState(): TournamentVMState {
        return TournamentVMState(
            tournament = activeTournament.value!!,
            registeredPlayers = registeredPlayers.value,
            nextPlayerId = nextPlayerId.value,
            currentRoundPairings = currentRoundPairings.value,
            isGrouped = isGrouped.value,
            currentGroup = currentGroup.value,
        )
    }

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

    fun findPlayerById(id: Int): RegisteredPlayer?{
        return registeredPlayers.value.find{it.id == id}
    }

    fun removePlayer(index: Int){ tournamentStateHandler.removePlayer(index) }

    fun activatePlayer(index: Int){
        val newList = registeredPlayers.value.toMutableList()
        newList[index] = newList[index].copy(isActive = true)
        savedStateHandle["registeredPlayers"] = newList.toList()
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

    fun save(context: Context): Boolean{

        if (filename.value.isEmpty()){
            throw Exception("Filename cannot be empty string")
        }

        if(filename.value in listTournaments(context)){
            if (!deleteTournament(
                    context = context,
                    filename = filename.value
                )
            ){
                return false
            }
        }

        val data = getVMState()

        if(isGrouped.value){
            var success: Boolean
            groupMap.value.keys.forEachIndexed{ i, group ->
                success = saveGroup(
                    context = context,
                    filename = filename.value,
                    groupIndex = i,
                    data = groupMap.value[group]!!
                )
                if(!success){ return false }
            }
            return true
        }

        return saveTournament(
            context = context,
            data = data,
            filename = filename.value
        )
    }

    fun saveAs(
        newFilename: String,
        context: Context,
        onError: () -> Unit,
        onSuccess: () -> Unit,
        overWrite: Boolean = false,
        confirmOverWrite: () -> Unit = {}
    ){

        if (newFilename.isEmpty()){
            throw Exception("Filename cannot be empty string")
        }

        val files = listTournaments(context)
        if(newFilename in files && !overWrite){
            confirmOverWrite()
            return
        }

        savedStateHandle["filename"] = newFilename

        if (save(context = context)){
            onSuccess()
            return
        }
        onError()
    }

    fun load (
        context: Context,
        filename: String
    ): Boolean{
        val loadedGroupMap = mutableMapOf<String, TournamentVMState>()

        val loadedData = loadTournament(
            context = context,
            filename = filename,
            groupMap = loadedGroupMap
        )

        if(loadedData == null){
            return false
        }

        savedStateHandle["filename"] = filename
        setVMState(loadedData)

        if(isGrouped.value){
            savedStateHandle["groupMap"] = loadedGroupMap
        }

        return true
    }

    fun delete(
        context: Context,
        filename: String,
    ): Boolean{
        return deleteTournament(
            context = context,
            filename = filename
        )
    }

    fun getFileList(context: Context): List<String>{
        return listTournaments(
            context = context
        )
    }

    fun exportResults(
        context: Context,
        uri: Uri?,
        onError: () -> Unit,
    ){
        val tieBreaks = activeTournament.value!!.tieBreaks
        val sortedPlayers = registeredPlayers.value.sortedWith(
            compareByDescending<RegisteredPlayer> { it.score }.thenComparator
            { a, b ->
                var diff = 0f
                for (i in tieBreaks.indices) {
                    val tieBreak = tieBreaks[i]
                    diff = tieBreak.calculate(a, registeredPlayers.value) - tieBreak.calculate(
                        b,
                        registeredPlayers.value
                    )
                    if (diff != 0f) {
                        break
                    }
                }
                (-2 * diff).toInt()
            }
        )

        com.example.tammer_manager.data.export_import.exportResults(
            context = context,
            uri = uri,
            onError = onError,
            players = sortedPlayers,
            tournament = activeTournament.value!!,
            group = currentGroup.value
        )
    }
}