package com.example.tammer_manager.data.export_import

import android.content.Context
import android.net.Uri
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import com.example.tammer_manager.data.tournament_admin.enums.TieBreak
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

fun exportResults(
    players: List<RegisteredPlayer>,
    tournament: Tournament,
    tieBreaks: List<TieBreak>,
    context: Context,
    uri: Uri?,
    onError: () -> Unit
){
    if (uri == null){
        return
    }

    val roundsCompleted = tournament.roundsCompleted
    val maxRounds = tournament.maxRounds

    try{

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet()

        val baseStyle = workbook.createCellStyle()
        val boldStyle = workbook.createCellStyle()
        val tableHeaderStyle = workbook.createCellStyle()
        val workbookHeaderStyle = workbook.createCellStyle()

        setCellStyles(
            workbook = workbook,
            baseStyle = baseStyle,
            boldStyle =  boldStyle,
            tableHeaderStyle = tableHeaderStyle,
            workbookHeaderStyle = workbookHeaderStyle
        )

        val workbookHeaderRow = sheet.createRow(0)
        val workBookHeader = workbookHeaderRow.createCell(0)
        workBookHeader.cellStyle = workbookHeaderStyle
        workBookHeader.setCellValue(tournament.name)

        val subHeaderRow = sheet.createRow(workbookHeaderRow.rowNum + 2)
        val subHeader = subHeaderRow.createCell(0)
        subHeader.cellStyle = boldStyle
        subHeader.setCellValue(
            if (roundsCompleted == 0) "Participants:"
            else if (roundsCompleted < maxRounds) "Standings after $roundsCompleted out of $maxRounds rounds:"
            else "Final standings:"
        )

        val tableHeaderRowIndex = subHeaderRow.rowNum + 2

        generateTableHeader(
            sheet = sheet,
            roundsCompleted = roundsCompleted,
            tieBreaks = tieBreaks,
            tableHeaderRowIndex = tableHeaderRowIndex,
            tableHeaderStyle = tableHeaderStyle
        )


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
    tableHeaderStyle: XSSFCellStyle,
    workbookHeaderStyle: XSSFCellStyle
){
    val baseFont = workbook.createFont()
    baseFont.fontHeightInPoints = 12
    baseStyle.setFont(baseFont)

    val boldFont = workbook.createFont()
    boldFont.fontHeightInPoints = 12
    boldFont.bold = true
    boldStyle.setFont(boldFont)

    val tableHeaderFont = workbook.createFont()
    tableHeaderFont.bold = true
    tableHeaderFont.fontHeightInPoints = 12
    tableHeaderStyle.setFont(tableHeaderFont)
    tableHeaderStyle.alignment = HorizontalAlignment.CENTER
    tableHeaderStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
    tableHeaderStyle.setFillForegroundColor(XSSFColor(ByteArray(3){ 0xD3.toByte()}))

    val workbookHeaderFont = workbook.createFont()
    workbookHeaderFont.bold = true
    workbookHeaderFont.fontHeightInPoints = 14
    workbookHeaderStyle.setFont(workbookHeaderFont)
}

fun generateTableHeader(
    sheet: XSSFSheet,
    roundsCompleted: Int,
    tieBreaks: List<TieBreak>,
    tableHeaderRowIndex: Int,
    tableHeaderStyle: XSSFCellStyle,
){
    val tableHeaderRow = sheet.createRow(tableHeaderRowIndex)
    val rankCol = tableHeaderRow.createCell(0)
    rankCol.setCellValue("Rank")

    val tpnCol = tableHeaderRow.createCell(rankCol.columnIndex + 1)
    tpnCol.setCellValue("SNo.")

    val nameCol = tableHeaderRow.createCell(tpnCol.columnIndex + 1)
    nameCol.setCellValue("Name")

    val ratingCol = tableHeaderRow.createCell(nameCol.columnIndex + 1)
    ratingCol.setCellValue("Rtg")

    for (i in 1 until roundsCompleted + 1){
        tableHeaderRow
            .createCell(ratingCol.columnIndex + (3 * i - 2))
            .setCellValue("$i.Rd.")
    }

    val pointsCol = tableHeaderRow.createCell(ratingCol.columnIndex + 3 * roundsCompleted + 1)
    pointsCol.setCellValue("Pts")

    tieBreaks.forEachIndexed { i, it ->
        tableHeaderRow
            .createCell(pointsCol.columnIndex + i + 1)
            .setCellValue(it.abbreviation)
    }

    tableHeaderRow.cellIterator().forEach { it.cellStyle = tableHeaderStyle }
}