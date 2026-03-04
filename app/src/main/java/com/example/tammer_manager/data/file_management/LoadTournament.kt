package com.example.tammer_manager.data.file_management

import android.content.Context
import com.example.tammer_manager.viewmodels.TournamentVMState
import kotlinx.serialization.json.Json
import java.io.File


fun loadTournament(
    context: Context,
    filename: String,
    groupMap: MutableMap<String, TournamentVMState>
):TournamentVMState?{
    try {
        val file = File("${context.filesDir.path}/tournaments/$filename")

        if(file.isDirectory){
            if(file.list()?.isEmpty() ?: true){
                throw Exception("Tournament is marked as split but has no groups")
            }

            file.list()!!.forEach {
                File(file ,it).inputStream().use{ stream ->
                    val vmState = stream.bufferedReader().use { buffer ->
                        val json = buffer.readText()
                        Json.decodeFromString<TournamentVMState>(json)
                    }
                    groupMap[vmState.currentGroup] = vmState
                }
            }
            return groupMap.values.toList().sortedBy { it.currentGroup }[0]
        }

        file.inputStream().use { stream ->
            return stream.bufferedReader().use {
                val json = it.readText()
                Json.decodeFromString<TournamentVMState>(json)
            }
        }
    }catch (e: Exception){
        e.printStackTrace()
       return null
    }
}