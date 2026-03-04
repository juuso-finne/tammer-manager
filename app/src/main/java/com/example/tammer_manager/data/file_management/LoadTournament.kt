package com.example.tammer_manager.data.file_management

import android.content.Context
import com.example.tammer_manager.viewmodels.TournamentVMState
import kotlinx.serialization.json.Json
import java.io.File


fun loadTournament(
    context: Context,
    filename: String,
    groupIndex: Int?
):TournamentVMState?{
    val filePath = "${context.filesDir.path}/tournaments/$filename"
    return try {
        var file = File(filePath)

        if(file.isDirectory){
            if(file.list()?.isEmpty() ?: true){
                throw Exception("Tournament is marked as split but has no groups")
            }
            file = File(filePath + "/group${groupIndex ?: 0}")
        }

        file.inputStream().use { stream ->
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