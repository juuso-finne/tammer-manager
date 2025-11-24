package com.example.tammer_manager.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tammer_manager.data.player_import.ImportedPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.listOf


class PlayerPoolViewModel (
): ViewModel(){
    private val _playerPool = MutableStateFlow(listOf<ImportedPlayer>())
    val playerPool: StateFlow<List<ImportedPlayer>> = _playerPool.asStateFlow()

    fun emptyPlayerPool(){
        _playerPool.update { listOf<ImportedPlayer>() }
    }

    fun setPlayerPool(input: List<ImportedPlayer>){
        _playerPool.update { input }
    }
}