package com.example.tammer_manager.data.file_management

import android.content.Context
import com.example.tammer_manager.viewmodels.TournamentVMState
import kotlinx.serialization.json.Json
import java.io.File

fun saveGroup(
    context: Context,
    filename: String,
    groupIndex: Int,
    data: TournamentVMState
): Boolean{

    val directory = File("${context.filesDir.path}/tournaments/$filename")

    if(!directory.exists()){
        directory.mkdirs()
    }

    return try {
        val json = Json.encodeToString(data)
        File(directory, "group$groupIndex").outputStream().use{
            it.write(json.toByteArray())
        }
        true
    }catch (e: Exception){
        e.printStackTrace()
        false
    }
}