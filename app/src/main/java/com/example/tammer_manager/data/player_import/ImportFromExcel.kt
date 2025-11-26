package com.example.tammer_manager.data.player_import

import android.content.Context
import android.net.Uri

import com.example.tammer_manager.viewmodels.PlayerPoolViewModel
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import kotlin.reflect.typeOf

class InvalidRatingException(message: String): Exception(message)

fun importFromExcel(
    vmPlayerPool: PlayerPoolViewModel,
    context: Context,
    onError: (String) -> Unit,
    onSuccess: () -> Unit,
    uri: Uri?
){
    val tempList = mutableListOf<ImportedPlayer>()
    uri?.let {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val workbook = XSSFWorkbook(stream)
                val sheet = workbook.getSheetAt(0)
                val numRows = sheet.getPhysicalNumberOfRows()

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
            onError(message)
            return
        }
    }
    vmPlayerPool.setPlayerPool(tempList)
    onSuccess()
}