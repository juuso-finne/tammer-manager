package com.example.tammer_manager.data.file_management

import android.content.Context
import java.io.File

fun listTournaments(context: Context): List<String>{
    val directory = File(context.filesDir, "tournaments")

    return try{
        directory.list()?.toList() ?: listOf()
    }catch (e: Exception){
        e.printStackTrace()
        listOf()
    }
}