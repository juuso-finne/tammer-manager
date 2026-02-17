package com.example.tammer_manager.data.file_management


import android.content.Context
import com.example.tammer_manager.viewmodels.TournamentVMState
import kotlinx.serialization.json.Json
import java.io.File

fun saveTournament(
    context: Context,
    fileName: String,
    data: TournamentVMState
): Boolean{

    val directory = File(context.filesDir, "tournaments")

    if(!directory.exists()){
        directory.mkdirs()
    }

    return try {
        val json = Json.encodeToString(data)
        File(directory, fileName).outputStream().use{
            it.write(json.toByteArray())
        }
        true
    }catch (e: Exception){
        e.printStackTrace()
        false
    }
}