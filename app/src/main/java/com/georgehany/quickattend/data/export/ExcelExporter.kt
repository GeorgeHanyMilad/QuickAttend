package com.georgehany.quickattend.data.export

import android.content.Context
import android.content.Intent
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

        var workbook: XSSFWorkbook? = null

        return try {

            workbook = XSSFWorkbook()

            val sheet = workbook.createSheet("Attendance Summary")

            // ==============================
            // STYLES
            // ==============================

            val headerStyle = workbook.createCellStyle().apply {

                val font = workbook.createFont().apply {
                    bold = true
                    color = IndexedColors.WHITE.index
                }

                setFont(font)

                fillForegroundColor =
                    IndexedColors.DARK_BLUE.index

                fillPattern =
                    FillPatternType.SOLID_FOREGROUND

                alignment =
                    HorizontalAlignment.CENTER
            }

            val summaryTitleStyle =
                workbook.createCellStyle().apply {

                    val font = workbook.createFont().apply {
                        bold = true
                        color = IndexedColors.DARK_BLUE.index
                    }

                    setFont(font)
                }

            // ==============================
            // ATTENDANCE DATA
            // ==============================

            val recordsMap =
                records.associateBy { it.studentId }

            val presentCount =
                students.count { student ->
                    recordsMap[student.studentId]?.isPresent == true
                }

            val notPresentCount =
                students.size - presentCount

            val totalStudents =
                students.size

            val attendanceRate =
                if (totalStudents > 0) {
                    (presentCount.toDouble() /
                            totalStudents.toDouble()) * 100.0
                } else {
                    0.0
                }

            // ==============================
            // TIME FORMAT
            // ==============================

            val timeFormatter =
                SimpleDateFormat(
                    "hh:mm a",
                    Locale.ENGLISH
                )

            val startTimeStr =
                session.startTimeMs?.let {
                    timeFormatter.format(Date(it))
                } ?: "N/A"

            val endTimeStr =
                session.endTimeMs?.let {
                    timeFormatter.format(Date(it))
                } ?: "N/A"

            var rowNum = 0

            // ==============================
            // TITLE
            // ==============================

            val titleRow =
                sheet.createRow(rowNum++)

            titleRow.createCell(0).apply {

                setCellValue(
                    "Attendance Report - ${session.sectionName}"
                )

                cellStyle =
                    summaryTitleStyle
            }

            rowNum++

            // ==============================
            // SUMMARY
            // ==============================

            val summaryItems =
                listOf(
                    "Section Name:" to session.sectionName,
                    "Date:" to session.date,
                    "Start Time:" to startTimeStr,
                    "End Time:" to endTimeStr,
                    "Total Students:" to totalStudents.toString(),
                    "Present:" to presentCount.toString(),
                    "Absent:" to notPresentCount.toString(),
                    "Attendance Rate:" to
                            String.format(
                                Locale.ENGLISH,
                                "%.1f%%",
                                attendanceRate
                            )
                )

            for ((key, value) in summaryItems) {

                val row =
                    sheet.createRow(rowNum++)

                row.createCell(0).apply {

                    setCellValue(key)

                    cellStyle =
                        summaryTitleStyle
                }

                row.createCell(1)
                    .setCellValue(value)
            }

            rowNum++

            // ==============================
            // ATTENDANCE TABLE TITLE
            // ==============================

            val tableTitleRow =
                sheet.createRow(rowNum++)

            tableTitleRow.createCell(0).apply {

                setCellValue(
                    "Attendance List"
                )

                cellStyle =
                    summaryTitleStyle
            }

            // ==============================
            // TABLE HEADERS
            // ==============================

            val headers =
                listOf(
                    "Student ID",
                    "Student Name",
                    "Status",
                    "Date",
                    "Time",
                    "Section"
                )

            val headerRow =
                sheet.createRow(rowNum++)

            for (i in headers.indices) {

                val cell =
                    headerRow.createCell(i)

                cell.setCellValue(headers[i])

                cell.cellStyle =
                    headerStyle
            }

            // ==============================
            // ALL STUDENTS
            // ==============================

            for (student in students) {

                val record =
                    recordsMap[student.studentId]

                val isPresent =
                    record?.isPresent == true

                val status =
                    if (isPresent) {
                        "Present"
                    } else {
                        "Absent"
                    }

                val recordTime =
                    record?.timestamp?.let {
                        timeFormatter.format(
                            Date(it)
                        )
                    } ?: "-"

                val row =
                    sheet.createRow(rowNum++)

                row.createCell(0)
                    .setCellValue(
                        student.studentId
                    )

                row.createCell(1)
                    .setCellValue(
                        student.studentName
                    )

                row.createCell(2)
                    .setCellValue(
                        status
                    )

                row.createCell(3)
                    .setCellValue(
                        session.date
                    )

                row.createCell(4)
                    .setCellValue(
                        recordTime
                    )

                row.createCell(5)
                    .setCellValue(
                        session.sectionName
                    )
            }

            // ==============================
            // COLUMN WIDTHS
            // ==============================

            for (i in headers.indices) {

                sheet.setColumnWidth(
                    i,
                    5000
                )
            }

            // ==============================
            // FILE NAME
            // ==============================

            val sanitizedSection =
                session.sectionName.replace(
                    Regex("[^a-zA-Z0-9_]"),
                    "_"
                )

            val sanitizedDate =
                session.date.replace(
                    Regex("[^a-zA-Z0-9_]"),
                    "_"
                )

            val fileName =
                "${sanitizedSection}_${sanitizedDate}.xlsx"

            // ==============================
            // EXPORT DIRECTORY
            // ==============================

            val exportDir =
                File(
                    context.cacheDir,
                    "exported_reports"
                )

            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            // ==============================
            // CREATE FILE
            // ==============================

            val file =
                File(
                    exportDir,
                    fileName
                )

            FileOutputStream(file).use { output ->

                workbook.write(output)

                output.flush()
            }

            file

        } catch (e: Exception) {

            e.printStackTrace()

            null

        } finally {

            try {
                workbook?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ==========================================
    // SHARE EXCEL FILE
    // ==========================================

    fun shareExportedFile(
        context: Context,
        file: File,
        sectionName: String
    ) {

        try {

            // Make sure the file actually exists
            if (!file.exists()) {

                throw IllegalStateException(
                    "Exported Excel file does not exist."
                )
            }

            // FileProvider authority
            val authority =
                "${context.packageName}.fileprovider"

            // Convert local file to content:// URI
            val contentUri =
                FileProvider.getUriForFile(
                    context,
                    authority,
                    file
                )

            // Create SEND intent
            val shareIntent =
                Intent(Intent.ACTION_SEND).apply {

                    type =
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

                    putExtra(
                        Intent.EXTRA_STREAM,
                        contentUri
                    )

                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        "Attendance Report - $sectionName"
                    )

                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    clipData =
                        android.content.ClipData.newRawUri(
                            "Attendance Excel",
                            contentUri
                        )
                }

            // Android Share Sheet
            val chooserIntent =
                Intent.createChooser(
                    shareIntent,
                    "Share Attendance Excel"
                )

            chooserIntent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            context.startActivity(
                chooserIntent
            )

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
}
