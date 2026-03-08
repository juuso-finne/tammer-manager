package com.example.tammer_manager.data.export_import

import android.content.Context
import android.net.Uri
import com.example.tammer_manager.data.tournament_admin.classes.RegisteredPlayer
import com.example.tammer_manager.data.tournament_admin.classes.Tournament
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.data.tournament_admin.enums.TieBreak
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

fun exportResults(
    context: Context,
    uri: Uri?,
    onError: () -> Unit,
    players: List<RegisteredPlayer>,
    tournament: Tournament,
    group: String
){
    if (uri == null){
        return
    }

    try {
        val workbook = XSSFWorkbook()
        generateWorkbook(
            players = players,
            tournament = tournament,
            workbook = workbook,
            group = group
        )

        val outputStream = context.contentResolver.openOutputStream(uri)
        outputStream?.use{ stream ->
            workbook.write(stream)
            workbook.close()
        }

    } catch(e: Exception){
        e.printStackTrace()
        onError()
    }

}

fun generateWorkbook(
    players: List<RegisteredPlayer>,
    tournament: Tournament,
    workbook: XSSFWorkbook,
    group: String
){

    val roundsCompleted = tournament.roundsCompleted
    val maxRounds = tournament.maxRounds


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
    workBookHeader.setCellValue(tournament.name + if(group.isNotEmpty()) ", group $group" else "")

    val subHeaderRow = sheet.createRow(sheet.lastRowNum + 2)
    val subHeader = subHeaderRow.createCell(0)
    subHeader.cellStyle = boldStyle
    subHeader.setCellValue(
        if (roundsCompleted == 0) "Participants:"
        else if (roundsCompleted < maxRounds) "Standings after $roundsCompleted out of $maxRounds rounds:"
        else "Final standings:"
    )

    generateTableHeader(
        sheet = sheet,
        roundsCompleted = roundsCompleted,
        tieBreaks = tournament.tieBreaks,
        row = sheet.createRow(sheet.lastRowNum + 2),
        tableHeaderStyle = tableHeaderStyle
    )

    players.forEachIndexed { i, it ->
        populatePlayerRow(
            rank = i + 1,
            row = sheet.createRow(sheet.lastRowNum + 1),
            player = it,
            players = players,
            tieBreaks = tournament.tieBreaks,
            roundsCompleted = roundsCompleted,
            baseStyle = baseStyle
        )
    }

    sheet.setColumnWidth(2, 25 * 256)

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
    baseStyle.borderLeft = BorderStyle.THIN
    baseStyle.borderRight = BorderStyle.THIN
    baseStyle.borderTop = BorderStyle.THIN
    baseStyle.borderBottom = BorderStyle.THIN

    val boldFont = workbook.createFont()
    boldFont.fontHeightInPoints = 12
    boldFont.bold = true
    boldStyle.setFont(boldFont)

    tableHeaderStyle.cloneStyleFrom(baseStyle)
    tableHeaderStyle.setFont(boldFont)
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
    val firstRoundColumnIndex =  row.lastCellNum

    for (i in 0 until roundsCompleted){
        val regionStartIndex = firstRoundColumnIndex + 3 * i
        row
            .createCell(regionStartIndex)
            .setCellValue("${i + 1}.Rd.")

        sheet.addMergedRegion(CellRangeAddress(
            /* firstRow = */ row.rowNum,
            /* lastRow = */ row.rowNum,
            /* firstCol = */ regionStartIndex,
            /* lastCol = */ regionStartIndex + 2
        ))
    }

     row.createCell(firstRoundColumnIndex + 3 * roundsCompleted).setCellValue("Pts")

    tieBreaks.forEach {
        row
            .createCell(row.lastCellNum.toInt())
            .setCellValue(it.abbreviation)
    }

    row.cellIterator().forEach { it.cellStyle = tableHeaderStyle }
}

fun populatePlayerRow(
    rank: Int,
    row: XSSFRow,
    player: RegisteredPlayer,
    players: List<RegisteredPlayer>,
    tieBreaks: List<TieBreak>,
    roundsCompleted: Int,
    baseStyle: XSSFCellStyle,
){
    val missedRounds = MutableList(roundsCompleted){it + 1}

    row.createCell(0).setCellValue("$rank")
    row.createCell(row.lastCellNum.toInt()).setCellValue("${player.tpn}")
    row.createCell(row.lastCellNum.toInt()).setCellValue(player.fullName)
    row.createCell(row.lastCellNum.toInt()).setCellValue("${player.rating}")

    val firstRoundColIndex =  row.lastCellNum

    for (i in player.matchHistory.indices){
        val match = player.matchHistory[i]
        val round = match.round

        missedRounds.remove(round)

        val opponentColIndex = firstRoundColIndex + 3 * (round - 1)
        val colorColIndex = opponentColIndex + 1
        val resultColIndex = colorColIndex + 1

        if(match.opponentId == null){
            row.createCell(opponentColIndex).setCellValue("-")
            row.createCell(colorColIndex).setCellValue("-")
            row.createCell(resultColIndex).setCellValue("1")

            continue
        }

        val opponentRank = players.indexOfFirst { it.id == match.opponentId } + 1
        val result = if (match.result == 0.5f) "½" else "${match.result.toInt()}"
        val color = if(match.color == PlayerColor.WHITE) "w" else "b"

        row.createCell(opponentColIndex).setCellValue("$opponentRank")

        val existingResult = row.getCell(resultColIndex)?.stringCellValue
        row.createCell(resultColIndex).setCellValue(existingResult?.let{"$it $result"} ?: result)

        val existingColor = row.getCell(colorColIndex)?.stringCellValue
        row.createCell(colorColIndex).setCellValue(existingColor?.let{"$it $color"} ?: color)
    }

    missedRounds.forEach {
        val columnIndex = firstRoundColIndex + 3 * (it - 1)
        for (subIndex in 0 until 3){
            row.createCell(columnIndex + subIndex).setCellValue("-")
        }
    }

    val scoreAsText = if (player.score != 0.5f) "${player.score.toInt()}" else "" + if (player.score % 1.0 != 0.0) "½" else ""

    val scoreColIndex = firstRoundColIndex + 3 * roundsCompleted

    row.createCell(scoreColIndex).setCellValue(scoreAsText)

    tieBreaks.forEach {
        row.createCell(row.lastCellNum.toInt()).setCellValue("${it.calculate(player, players)}")
    }

    row.cellIterator().forEach {
        it.cellStyle = baseStyle
    }
}