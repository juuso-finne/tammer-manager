package com.example.tammer_manager.data.file_management

import android.content.Context
import com.example.tammer_manager.viewmodels.TournamentVMState
import kotlinx.serialization.json.Json
import java.io.File


fun loadTournament(
    context: Context,
    fileName: String,
):TournamentVMState?{
    return try {
        val directory = File(context.filesDir, "tournaments")
        File(directory, fileName).inputStream().use { stream ->
            stream.bufferedReader().use {
                val json = it.readText()
                Json.decodeFromString<TournamentVMState>(json)
            }
        }
    }catch (e: Exception){
        e.printStackTrace()
        null
    }
}