package com.georgehany.quickattend.data.parser

import android.content.ContentResolver
import android.net.Uri
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

data class RawStudent(
    val studentId: String,
    val studentName: String
)

sealed class ImportParseResult {
    data class Success(val students: List<RawStudent>) : ImportParseResult()
    data class ColumnMappingRequired(
        val columns: List<String>,
        val rawRows: List<List<String>>,
        val detectedIdColIndex: Int? = null,
        val detectedNameColIndex: Int? = null
    ) : ImportParseResult()
    data class Error(val message: String) : ImportParseResult()
}

object FileImporter {

    fun parseFile(contentResolver: ContentResolver, uri: Uri, fileName: String?): ImportParseResult {
        return try {
            val lowerName = (fileName ?: "").lowercase()
            val rawRows: List<List<String>> = if (lowerName.endsWith(".csv") || lowerName.endsWith(".txt")) {
                contentResolver.openInputStream(uri)?.use { parseTextFile(it) } ?: emptyList()
            } else {
                contentResolver.openInputStream(uri)?.use { parseExcelFile(it) } ?: emptyList()
            }

            if (rawRows.isEmpty()) {
                return ImportParseResult.Error("File is empty or could not be read.")
            }

            processRawRows(rawRows)
        } catch (e: Exception) {
            ImportParseResult.Error("Failed to parse file: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    private fun parseExcelFile(inputStream: InputStream): List<List<String>> {
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0) ?: return emptyList()
        val formatter = DataFormatter()
        val result = mutableListOf<List<String>>()

        for (row in sheet) {
            val rowList = mutableListOf<String>()
            val maxCell = row.lastCellNum.toInt()
            if (maxCell <= 0) continue
            var hasData = false
            for (i in 0 until maxCell) {
                val cell = row.getCell(i)
                val text = if (cell != null) formatter.formatCellValue(cell).trim() else ""
                if (text.isNotEmpty()) hasData = true
                rowList.add(text)
            }
            if (hasData) {
                result.add(rowList)
            }
        }
        workbook.close()
        return result
    }

    private fun parseTextFile(inputStream: InputStream): List<List<String>> {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val result = mutableListOf<List<String>>()
        var line: String? = reader.readLine()

        while (line != null) {
            val trimmed = line.trim()
            if (trimmed.isNotEmpty()) {
                val delimiter = when {
                    trimmed.contains("\t") -> "\t"
                    trimmed.contains(",") -> ","
                    trimmed.contains(";") -> ";"
                    trimmed.contains("|") -> "|"
                    else -> "\\s+"
                }
                val parts = trimmed.split(Regex(delimiter)).map { it.trim() }
                if (parts.any { it.isNotEmpty() }) {
                    result.add(parts)
                }
            }
            line = reader.readLine()
        }
        return result
    }

    private fun processRawRows(rawRows: List<List<String>>): ImportParseResult {
        var headerRowIndex = -1
        var idColIndex = -1
        var nameColIndex = -1

        val idKeywords = listOf("id", "student id", "st_id", "code", "student_code", "num", "number", "matric", "registration", "كود", "الرقم", "رقم القيد")
        val nameKeywords = listOf("name", "student name", "full name", "st_name", "student", "اسم", "الاسم", "اسم الطالب")

        // Search top 15 rows for potential headers
        for (r in 0 until minOf(15, rawRows.size)) {
            val row = rawRows[r]
            var foundId = -1
            var foundName = -1

            for (c in row.indices) {
                val cell = row[c].lowercase().trim()
                if (foundId == -1 && idKeywords.any { cell.contains(it) }) {
                    foundId = c
                }
                if (foundName == -1 && nameKeywords.any { cell.contains(it) }) {
                    foundName = c
                }
            }

            if (foundId != -1 && foundName != -1 && foundId != foundName) {
                headerRowIndex = r
                idColIndex = foundId
                nameColIndex = foundName
                break
            }
        }

        // Check columns count
        val maxCols = rawRows.maxOfOrNull { it.size } ?: 0
        if (maxCols == 0) return ImportParseResult.Error("No data found in file.")

        // Build header string representation
        val sampleHeaders: List<String> = if (headerRowIndex != -1) {
            rawRows[headerRowIndex]
        } else {
            (0 until maxCols).map { "Column ${it + 1}" }
        }

        if (headerRowIndex != -1 && idColIndex != -1 && nameColIndex != -1) {
            // Auto-detected columns!
            val students = extractStudentsFromRows(rawRows, headerRowIndex + 1, idColIndex, nameColIndex)
            if (students.isNotEmpty()) {
                return ImportParseResult.Success(students)
            }
        }

        // Heuristic fallback: if exactly 2 columns and no header found, col 0 = ID or Name, col 1 = Name or ID
        if (maxCols == 2 && rawRows.isNotEmpty()) {
            val startIdx = if (headerRowIndex != -1) headerRowIndex + 1 else 0
            val col0LooksNumeric = rawRows.drop(startIdx).take(5).all { r -> r.getOrNull(0)?.any { it.isDigit() } == true }
            val col1LooksNumeric = rawRows.drop(startIdx).take(5).all { r -> r.getOrNull(1)?.any { it.isDigit() } == true }

            if (col0LooksNumeric && !col1LooksNumeric) {
                val students = extractStudentsFromRows(rawRows, startIdx, 0, 1)
                if (students.isNotEmpty()) return ImportParseResult.Success(students)
            } else if (!col0LooksNumeric && col1LooksNumeric) {
                val students = extractStudentsFromRows(rawRows, startIdx, 1, 0)
                if (students.isNotEmpty()) return ImportParseResult.Success(students)
            }
        }

        // Return Mapping Required
        return ImportParseResult.ColumnMappingRequired(
            columns = sampleHeaders,
            rawRows = rawRows,
            detectedIdColIndex = if (idColIndex != -1) idColIndex else null,
            detectedNameColIndex = if (nameColIndex != -1) nameColIndex else null
        )
    }

    fun extractStudentsFromRows(
        rawRows: List<List<String>>,
        startRowIndex: Int,
        idColIndex: Int,
        nameColIndex: Int
    ): List<RawStudent> {
        val list = mutableListOf<RawStudent>()
        for (r in startRowIndex until rawRows.size) {
            val row = rawRows[r]
            val idVal = row.getOrNull(idColIndex)?.trim() ?: ""
            val nameVal = row.getOrNull(nameColIndex)?.trim() ?: ""
            if (idVal.isNotEmpty() || nameVal.isNotEmpty()) {
                list.add(
                    RawStudent(
                        studentId = if (idVal.isNotEmpty()) idVal else "S${list.size + 1}",
                        studentName = if (nameVal.isNotEmpty()) nameVal else "Student ${list.size + 1}"
                    )
                )
            }
        }
        return list
    }
}
