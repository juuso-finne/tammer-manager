package com.example.tammer_manager.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.tammer_manager.data.player_import.ImportedPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.listOf


class PlayerPoolViewModel (
    private val savedStateHadle: SavedStateHandle
): ViewModel(){
    val playerPool = savedStateHadle.getStateFlow("playerPool", listOf<ImportedPlayer>())

    fun emptyPlayerPool(){
        savedStateHadle["playerPool"] = listOf<ImportedPlayer>()
    }

    fun setPlayerPool(input: List<ImportedPlayer>){
        savedStateHadle["playerPool"] = input.toList()
    }
}