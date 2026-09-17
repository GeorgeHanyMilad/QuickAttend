package com.georgehany.quickattend.data.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.georgehany.quickattend.data.local.entity.AttendanceRecord
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.data.local.entity.Student
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
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

            val sheet = workbook.createSheet(
                "Attendance"
            )

            val headerFont =
                workbook.createFont().apply {
                    bold = true
                    color = IndexedColors.WHITE.index
                }

            val titleFont =
                workbook.createFont().apply {
                    bold = true
                    fontHeightInPoints = 16
                    color = IndexedColors.WHITE.index
                }

            val subtitleFont =
                workbook.createFont().apply {
                    bold = true
                    fontHeightInPoints = 11
                    color = IndexedColors.DARK_BLUE.index
                }

            val normalFont =
                workbook.createFont().apply {
                    fontHeightInPoints = 10
                }

            val headerStyle =
                workbook.createCellStyle().apply {
                    setFont(headerFont)
                    fillForegroundColor =
                        IndexedColors.DARK_BLUE.index
                    fillPattern =
                        FillPatternType.SOLID_FOREGROUND
                    alignment =
                        HorizontalAlignment.CENTER
                    verticalAlignment =
                        VerticalAlignment.CENTER

                    borderTop =
                        BorderStyle.THIN
                    borderBottom =
                        BorderStyle.THIN
                    borderLeft =
                        BorderStyle.THIN
                    borderRight =
                        BorderStyle.THIN
                }

            val titleStyle =
                workbook.createCellStyle().apply {
                    setFont(titleFont)
                    fillForegroundColor =
                        IndexedColors.BLUE.index
                    fillPattern =
                        FillPatternType.SOLID_FOREGROUND
                    alignment =
                        HorizontalAlignment.CENTER
                    verticalAlignment =
                        VerticalAlignment.CENTER
                }

            val subtitleStyle =
                workbook.createCellStyle().apply {
                    setFont(subtitleFont)
                    alignment =
                        HorizontalAlignment.LEFT
                    verticalAlignment =
                        VerticalAlignment.CENTER
                }

            val normalStyle =
                workbook.createCellStyle().apply {
                    setFont(normalFont)
                    verticalAlignment =
                        VerticalAlignment.CENTER
                    borderBottom =
                        BorderStyle.THIN
                    borderLeft =
                        BorderStyle.THIN
                    borderRight =
                        BorderStyle.THIN
                }

            val centerStyle =
                workbook.createCellStyle().apply {
                    setFont(normalFont)
                    alignment =
                        HorizontalAlignment.CENTER
                    verticalAlignment =
                        VerticalAlignment.CENTER
                    borderBottom =
                        BorderStyle.THIN
                    borderLeft =
                        BorderStyle.THIN
                    borderRight =
                        BorderStyle.THIN
                }

            val presentStyle =
                workbook.createCellStyle().apply {
                    setFont(normalFont)
                    alignment =
                        HorizontalAlignment.CENTER
                    verticalAlignment =
                        VerticalAlignment.CENTER
                    fillForegroundColor =
                        IndexedColors.LIGHT_GREEN.index
                    fillPattern =
                        FillPatternType.SOLID_FOREGROUND
                    borderBottom =
                        BorderStyle.THIN
                    borderLeft =
                        BorderStyle.THIN
                    borderRight =
                        BorderStyle.THIN
                }

            val absentStyle =
                workbook.createCellStyle().apply {
                    setFont(normalFont)
                    alignment =
                        HorizontalAlignment.CENTER
                    verticalAlignment =
                        VerticalAlignment.CENTER
                    fillForegroundColor =
                        IndexedColors.ROSE.index
                    fillPattern =
                        FillPatternType.SOLID_FOREGROUND
                    borderBottom =
                        BorderStyle.THIN
                    borderLeft =
                        BorderStyle.THIN
                    borderRight =
                        BorderStyle.THIN
                }

            /*
             * Title
             */
            val titleRow =
                sheet.createRow(0)

            titleRow.heightInPoints = 28f

            val titleCell =
                titleRow.createCell(0)

            titleCell.setCellValue(
                "EELU - Student Attendance"
            )

            titleCell.cellStyle =
                titleStyle

            sheet.addMergedRegion(
                org.apache.poi.ss.util.CellRangeAddress(
                    0,
                    0,
                    0,
                    4
                )
            )

            /*
             * Session information
             */
            val sectionRow =
                sheet.createRow(2)

            sectionRow.createCell(0).apply {
                setCellValue("Section")
                cellStyle = subtitleStyle
            }

            sectionRow.createCell(1).apply {
                setCellValue(session.sectionName)
                cellStyle = normalStyle
            }

            val dateRow =
                sheet.createRow(3)

            dateRow.createCell(0).apply {
                setCellValue("Date")
                cellStyle = subtitleStyle
            }

            dateRow.createCell(1).apply {
                setCellValue(session.date)
                cellStyle = normalStyle
            }

            val statusRow =
                sheet.createRow(4)

            statusRow.createCell(0).apply {
                setCellValue("Status")
                cellStyle = subtitleStyle
            }

            statusRow.createCell(1).apply {
                setCellValue(session.status)
                cellStyle = normalStyle
            }

            /*
             * Summary
             */
            val totalStudents =
                students.size

            val presentCount =
                records.count { it.isPresent }

            val absentCount =
                records.count { !it.isPresent }

            val attendanceRate =
                if (totalStudents > 0) {
                    (presentCount.toDouble() /
                        totalStudents.toDouble()) * 100.0
                } else {
                    0.0
                }

            val summaryRow =
                sheet.createRow(6)

            summaryRow.createCell(0).apply {
                setCellValue("Total Students")
                cellStyle = subtitleStyle
            }

            summaryRow.createCell(1).apply {
                setCellValue(totalStudents.toDouble())
                cellStyle = centerStyle
            }

            summaryRow.createCell(2).apply {
                setCellValue("Present")
                cellStyle = subtitleStyle
            }

            summaryRow.createCell(3).apply {
                setCellValue(presentCount.toDouble())
                cellStyle = centerStyle
            }

            summaryRow.createCell(4).apply {
                setCellValue(
                    String.format(
                        Locale.US,
                        "%.1f%%",
                        attendanceRate
                    )
                )
                cellStyle = centerStyle
            }

            val absentSummaryRow =
                sheet.createRow(7)

            absentSummaryRow.createCell(0).apply {
                setCellValue("Absent")
                cellStyle = subtitleStyle
            }

            absentSummaryRow.createCell(1).apply {
                setCellValue(absentCount.toDouble())
                cellStyle = centerStyle
            }

            /*
             * Attendance table
             */
            val tableHeaderRow =
                sheet.createRow(9)

            val headers = listOf(
                "#",
                "Student ID",
                "Student Name",
                "Attendance",
                "Recorded At"
            )

            headers.forEachIndexed { index, header ->
                tableHeaderRow
                    .createCell(index)
                    .apply {
                        setCellValue(header)
                        cellStyle = headerStyle
                    }
            }

            val recordsByStudent =
                records.associateBy {
                    it.studentId
                }

            students.forEachIndexed { index, student ->

                val row =
                    sheet.createRow(
                        10 + index
                    )

                val record =
                    recordsByStudent[student.studentId]

                row.createCell(0).apply {
                    setCellValue(
                        (index + 1).toDouble()
                    )
                    cellStyle = centerStyle
                }

                row.createCell(1).apply {
                    setCellValue(
                        student.studentId
                    )
                    cellStyle = normalStyle
                }

                row.createCell(2).apply {
                    setCellValue(
                        student.name
                    )
                    cellStyle = normalStyle
                }

                row.createCell(3).apply {

                    setCellValue(
                        when {
                            record == null -> "Not Recorded"
                            record.isPresent -> "Present"
                            else -> "Absent"
                        }
                    )

                    cellStyle = when {
                        record == null ->
                            centerStyle

                        record.isPresent ->
                            presentStyle

                        else ->
                            absentStyle
                    }
                }

                row.createCell(4).apply {

                    val timestamp =
                        record?.timestamp

                    if (timestamp != null) {

                        val formatted =
                            SimpleDateFormat(
                                "HH:mm:ss",
                                Locale.getDefault()
                            ).format(
                                Date(timestamp)
                            )

                        setCellValue(formatted)

                    } else {
                        setCellValue("-")
                    }

                    cellStyle = centerStyle
                }
            }

            /*
             * Column widths
             */
            sheet.setColumnWidth(
                0,
                8 * 256
            )

            sheet.setColumnWidth(
                1,
                20 * 256
            )

            sheet.setColumnWidth(
                2,
                38 * 256
            )

            sheet.setColumnWidth(
                3,
                18 * 256
            )

            sheet.setColumnWidth(
                4,
                18 * 256
            )

            sheet.createFreezePane(
                0,
                10
            )

            /*
             * Save file
             */
            val exportDirectory =
                File(
                    context.cacheDir,
                    "exported_reports"
                )

            if (!exportDirectory.exists()) {
                exportDirectory.mkdirs()
            }

            val safeSectionName =
                session.sectionName
                    .replace(
                        Regex("[^a-zA-Z0-9._-]"),
                        "_"
                    )

            val timestamp =
                SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    Locale.US
                ).format(Date())

            val file =
                File(
                    exportDirectory,
                    "Attendance_${safeSectionName}_${timestamp}.xlsx"
                )

            FileOutputStream(file).use { output ->
                workbook.write(output)
            }

            workbook.close()

            file

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareExportedFile(
        context: Context,
        file: File,
        sectionName: String
    ) {

        val uri =
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {

                type =
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

                putExtra(
                    Intent.EXTRA_STREAM,
                    uri
                )

                putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Attendance Report - $sectionName"
                )

                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

        context.startActivity(
            Intent.createChooser(
                shareIntent,
                "Share Attendance Report"
            ).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }
        )
    }
}
