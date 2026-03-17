package com.example.tammer_manager.viewmodels.tournamentVM

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.example.tammer_manager.data.file_management.deleteTournament
import com.example.tammer_manager.data.file_management.listTournaments
import com.example.tammer_manager.data.file_management.loadTournament
import com.example.tammer_manager.data.file_management.saveGroup
import com.example.tammer_manager.data.file_management.saveTournament
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import com.example.tammer_manager.viewmodels.TournamentVMState

class FileHandler(
    private val savedStateHandle: SavedStateHandle,
    private val vmStateHandler: VMStateHandler
) {
    fun save(context: Context): Boolean{

        val isGrouped = savedStateHandle.get<Boolean>("isGrouped")!!
        val filename = savedStateHandle.get<String>("filename")!!
        val groupMap = savedStateHandle.get<Map<String, TournamentVMState>>("groupMap")!!

        if (filename.isEmpty()){
            throw Exception("Filename cannot be empty string")
        }

        if(filename in listTournaments(context)){
            if (!deleteTournament(
                    context = context,
                    filename = filename
                )
            ){
                return false
            }
        }

        val data = vmStateHandler.getVMState()

        if(isGrouped){
            var success: Boolean
            groupMap.keys.forEachIndexed{ i, group ->
                success = saveGroup(
                    context = context,
                    filename = filename,
                    groupIndex = i,
                    data = groupMap[group]!!
                )
                if(!success){ return false }
            }
            return true
        }

        return saveTournament(
            context = context,
            data = data,
            filename = filename
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
        val isGrouped = savedStateHandle.get<Boolean>("isGrouped")!!

        val loadedData = loadTournament(
            context = context,
            filename = filename,
            groupMap = loadedGroupMap
        )

        if(loadedData == null){
            return false
        }

        savedStateHandle["filename"] = filename
        vmStateHandler.setVMState(loadedData)

        if(isGrouped){
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
        val activeTournament = savedStateHandle.get<Tournament>("tournament")!!
        val registeredPlayers = savedStateHandle.get<List<RegisteredPlayer>>("registeredPlayers")!!
        val currentGroup = savedStateHandle.get<String>("currentGroup")!!

        val tieBreaks = activeTournament.tieBreaks
        val sortedPlayers = registeredPlayers.sortedWith(
            compareByDescending<RegisteredPlayer> { it.score }.thenComparator
            { a, b ->
                var diff = 0f
                for (i in tieBreaks.indices) {
                    val tieBreak = tieBreaks[i]
                    diff = tieBreak.calculate(a, registeredPlayers) - tieBreak.calculate(
                        b,
                        registeredPlayers
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
            tournament = activeTournament,
            group = currentGroup
        )
    }
}