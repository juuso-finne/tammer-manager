package com.example.tammer_manager.data.file_management

import android.content.Context
import org.apache.commons.io.file.PathUtils.deleteFile
import java.io.File


fun deleteTournament(
    context: Context,
    filename: String,
): Boolean{
    try {
        File("${context.filesDir.path}/tournaments/$filename").deleteRecursively()
        return true
    }catch (e: Exception){
        e.printStackTrace()
        return false
    }
}