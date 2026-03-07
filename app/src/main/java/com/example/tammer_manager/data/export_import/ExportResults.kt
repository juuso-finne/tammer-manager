package com.example.tammer_manager.data.export_import

import android.content.Context
import android.net.Uri
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook

fun exportResults(
    players: List<RegisteredPlayer>,
    completedRounds: Int,
    maxRounds: Int,
    context: Context,
    uri: Uri?,
    onError: () -> Unit
){
    if (uri == null){
        return
    }

    try{
    } catch (e: Exception){
        println(e.message)
        e.printStackTrace()
        onError()
    }
}

fun setCellStyles(
    workbook: XSSFWorkbook,
    baseStyle: XSSFCellStyle,
    boldStyle: XSSFCellStyle,
    headerStyle: XSSFCellStyle
){
    val headerFont = workbook.createFont()
    headerFont.bold = true
    headerFont.fontHeightInPoints = 14
    headerStyle.setFont(headerFont)

    val baseFont = workbook.createFont()
    baseFont.fontHeightInPoints = 12
    baseStyle.setFont(baseFont)

    val boldFont = workbook.createFont()
    boldFont.bold = true
    boldFont.fontHeightInPoints = 12
    boldStyle.setFont(boldFont)
}