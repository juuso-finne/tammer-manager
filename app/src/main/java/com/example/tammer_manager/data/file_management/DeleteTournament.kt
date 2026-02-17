package com.example.tammer_manager.data.file_management

import android.content.Context
import org.apache.commons.io.file.PathUtils.deleteFile
import java.io.File


fun deleteTournament(
    context: Context,
    fileName: String,
): Boolean{
    val directory = File(context.filesDir, "tournaments")

    try {
        deleteFile(File(directory, fileName).toPath())
        return true
    }catch (e: Exception){
        e.printStackTrace()
        return false
    }
}