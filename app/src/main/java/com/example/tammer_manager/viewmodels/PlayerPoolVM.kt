package com.example.tammer_manager.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.tammer_manager.data.player_import.ImportedPlayer
import kotlin.collections.listOf


class PlayerPoolViewModel (
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    val playerPool = savedStateHandle.getStateFlow("playerPool", listOf<ImportedPlayer>())

    fun emptyPlayerPool(){
        savedStateHandle["playerPool"] = listOf<ImportedPlayer>()
    }

    fun setPlayerPool(input: List<ImportedPlayer>){
        savedStateHandle["playerPool"] = input.toList()
    }
}