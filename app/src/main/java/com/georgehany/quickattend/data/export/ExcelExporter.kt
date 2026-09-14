package com.georgehany.quickattend.data.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.georgehany.quickattend.data.local.entity.AttendanceRecord
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.data.local.entity.Student
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExcelExporter {

    fun exportSessionToExcel(
        context: Context,
        session: Session,
        students: List<Student>,
        records: List<AttendanceRecord>
    ): File? {
        return try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Attendance Summary")

            // Styles
            val headerStyle = workbook.createCellStyle().apply {
                val font = workbook.createFont().apply {
                    bold = true
                    color = IndexedColors.WHITE.index
                }
                setFont(font)
                fillForegroundColor = IndexedColors.NAVY.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
                alignment = HorizontalAlignment.CENTER
            }

            val summaryTitleStyle = workbook.createCellStyle().apply {
                val font = workbook.createFont().apply {
                    bold = true
                    color = IndexedColors.DARK_BLUE.index
                }
                setFont(font)
            }

            val presentMap = records.associateBy { it.studentId }
            val presentStudents = students.filter { presentMap[it.studentId]?.isPresent == true }
            val presentCount = presentStudents.size
            val notPresentCount = session.totalStudents - presentCount
            val attendanceRate = if (session.totalStudents > 0) {
                (presentCount.toDouble() / session.totalStudents.toDouble()) * 100.0
            } else 0.0

            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val startTimeStr = session.startTimeMs?.let { timeFormatter.format(Date(it)) } ?: "N/A"
            val endTimeStr = session.endTimeMs?.let { timeFormatter.format(Date(it)) } ?: "N/A"

            var rowNum = 0

            // Title Header Block
            val titleRow = sheet.createRow(rowNum++)
            titleRow.createCell(0).apply {
                setCellValue("Attendance Report – ${session.sectionName}")
                cellStyle = summaryTitleStyle
            }

            rowNum++ // Blank line

            // Summary Information Block
            val summaryItems = listOf(
                "Section Name:" to session.sectionName,
                "Date:" to session.date,
                "Start Time:" to startTimeStr,
                "End Time:" to endTimeStr,
                "Total Students:" to session.totalStudents.toString(),
                "Present:" to presentCount.toString(),
                "Not Present:" to notPresentCount.toString(),
                "Attendance Rate:" to String.format(Locale.ENGLISH, "%.1f%%", attendanceRate)
            )

            for ((key, value) in summaryItems) {
                val row = sheet.createRow(rowNum++)
                row.createCell(0).apply {
                    setCellValue(key)
                    cellStyle = summaryTitleStyle
                }
                row.createCell(1).setCellValue(value)
            }

            rowNum++ // Blank line

            // Table Section Header
            val tableTitleRow = sheet.createRow(rowNum++)
            tableTitleRow.createCell(0).apply {
                setCellValue("Present Students List")
                cellStyle = summaryTitleStyle
            }

            // Column Headers
            val headers = listOf("Student ID", "Student Name", "Date", "Time", "Section")
            val headerRow = sheet.createRow(rowNum++)
            for (i in headers.indices) {
                val cell = headerRow.createCell(i)
                cell.setCellValue(headers[i])
                cell.cellStyle = headerStyle
            }

            // Data Rows
            for (student in presentStudents) {
                val rec = presentMap[student.studentId]
                val recordTimeStr = rec?.timestamp?.let { timeFormatter.format(Date(it)) } ?: startTimeStr
                val row = sheet.createRow(rowNum++)
                row.createCell(0).setCellValue(student.studentId)
                row.createCell(1).setCellValue(student.studentName)
                row.createCell(2).setCellValue(session.date)
                row.createCell(3).setCellValue(recordTimeStr)
                row.createCell(4).setCellValue(session.sectionName)
            }

            // Auto-fit columns
            for (i in headers.indices) {
                sheet.autoSizeColumn(i)
            }

            // Save to File
            val sanitizedSection = session.sectionName.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val sanitizedDate = session.date.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val fileName = "${sanitizedSection}_$sanitizedDate.xlsx"

            val exportDir = File(context.cacheDirectory, "exported_reports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val file = File(exportDir, fileName)
            FileOutputStream(file).use { out ->
                workbook.write(out)
            }
            workbook.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareExportedFile(context: Context, file: File, sectionName: String) {
        val authority = "${context.packageName}.fileprovider"
        val contentUri: Uri = FileProvider.getUriForFile(context, authority, file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "Attendance Report - $sectionName")
            putExtra(Intent.EXTRA_TEXT, "Attached attendance report for $sectionName.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Share Attendance Excel")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
