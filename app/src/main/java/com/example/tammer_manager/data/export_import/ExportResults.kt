package com.example.tammer_manager.data.export_import

import android.content.Context
import android.net.Uri
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import com.example.tammer_manager.data.tournament_admin.enums.TieBreak
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFRow
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

        val subHeaderRow = sheet.createRow(sheet.lastRowNum + 2)
        val subHeader = subHeaderRow.createCell(0)
        subHeader.cellStyle = boldStyle
        subHeader.setCellValue(
            if (roundsCompleted == 0) "Participants:"
            else if (roundsCompleted < maxRounds) "Standings after $roundsCompleted out of $maxRounds rounds:"
            else "Final standings:"
        )

        val tableHeaderRow = sheet.createRow(sheet.lastRowNum + 2)

        generateTableHeader(
            sheet = sheet,
            roundsCompleted = roundsCompleted,
            tieBreaks = tieBreaks,
            row = tableHeaderRow,
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
    row: XSSFRow,
    tableHeaderStyle: XSSFCellStyle,
){
    row.createCell(0).setCellValue("Rank")
    row.createCell(row.lastCellNum.toInt()).setCellValue("SNo.")
    row.createCell(row.lastCellNum.toInt()).setCellValue("Name")
    row.createCell(row.lastCellNum.toInt()).setCellValue("Rtg")

    for (i in 0 until roundsCompleted){
        val regionStartIndex = row.lastCellNum.toInt() + 3 * i
        row
            .createCell(regionStartIndex)
            .setCellValue("$i.Rd.")

        sheet.addMergedRegion(CellRangeAddress(
            row.rowNum,
            row.rowNum,
            regionStartIndex,
            regionStartIndex + 2
        ))
    }

     row.createCell(row.lastCellNum.toInt() + 3 * roundsCompleted).setCellValue("Pts")

    tieBreaks.forEachIndexed { i, it ->
        row
            .createCell(row.lastCellNum.toInt() + i)
            .setCellValue(it.abbreviation)
    }

    row.cellIterator().forEach { it.cellStyle = tableHeaderStyle }
}