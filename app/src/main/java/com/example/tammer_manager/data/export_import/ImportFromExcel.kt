package com.example.tammer_manager.data.export_import

import android.content.Context
import android.net.Uri

import com.example.tammer_manager.viewmodels.PlayerPoolViewModel
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class InvalidRatingException(message: String): Exception(message)

fun importFromExcel(
    context: Context,
    uri: Uri?,
    output: MutableList<ImportedPlayer>
){
    val tempList = mutableListOf<ImportedPlayer>()
    uri?.let {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val workbook = XSSFWorkbook(stream)
                val sheet = workbook.getSheetAt(0)
                val numRows = sheet.physicalNumberOfRows

                for(i in 0..<numRows){
                    val name = sheet.getRow(i).getCell(1).toString()
                    var rating = 0
                    try {
                        rating = sheet.getRow(i).getCell(2).numericCellValue.toInt()
                    } catch (e: Exception){
                        throw InvalidRatingException("Error! Invalid rating on row ${i + 1}")
                    }
                    tempList.add(ImportedPlayer(
                        fullName = name,
                        rating = rating
                    ))
                }
            }
            inputStream?.close()
        } catch (e: Exception){
            var message = "Error loading file"
            if (e is InvalidRatingException){
                message = e.message.toString()
            }
            throw Exception (message)
        }
    }
    output.addAll(tempList)
    return
}