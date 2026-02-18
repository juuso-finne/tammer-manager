package com.example.tammer_manager.data.file_management

import android.content.Context
import org.apache.commons.io.file.PathUtils.deleteFile
import java.io.File


fun deleteTournament(
    context: Context,
    filename: String,
): Boolean{
    val directory = File(context.filesDir, "tournaments")

    try {
        deleteFile(File(directory, filename).toPath())
        return true
    }catch (e: Exception){
        e.printStackTrace()
        return false
    }
}