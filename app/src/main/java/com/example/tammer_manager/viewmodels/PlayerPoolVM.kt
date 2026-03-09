package com.example.tammer_manager.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.tammer_manager.data.export_import.ImportedPlayer
import com.example.tammer_manager.data.export_import.importFromExcel
import kotlin.collections.listOf


class PlayerPoolViewModel (
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    val playerPool = savedStateHandle.getStateFlow("playerPool", listOf<ImportedPlayer>())

    fun importPlayers(
        context: Context,
        uri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        val output = mutableListOf<ImportedPlayer>()

        try{
            importFromExcel(
                context = context,
                uri = uri,
                output = output
            )

            savedStateHandle["playerPool"] = output.toList()
            onSuccess()
        } catch(e : Exception){
            onError(e.message ?: "Error loading file")
        }
    }
}