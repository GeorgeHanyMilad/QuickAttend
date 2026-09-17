package com.georgehany.quickattend.data.parser

import android.content.ContentResolver
import android.net.Uri
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Locale

data class RawStudent(
    val studentId: String,
    val studentName: String
)

sealed class ImportParseResult {

    data class Success(
        val students: List<RawStudent>
    ) : ImportParseResult()

    data class ColumnMappingRequired(
        val columns: List<String>,
        val rawRows: List<List<String>>,
        val detectedIdColIndex: Int? = null,
        val detectedNameColIndex: Int? = null
    ) : ImportParseResult()

    data class Error(
        val message: String
    ) : ImportParseResult()
}

object FileImporter {

    private val idKeywords = listOf(
        "id",
        "student id",
        "student_id",
        "st_id",
        "code",
        "student code",
        "student_code",
        "number",
        "student number",
        "registration",
        "registration number",
        "matric",
        "رقم",
        "الرقم",
        "كود",
        "رقم الطالب",
        "رقم القيد"
    )

    private val nameKeywords = listOf(
        "name",
        "student name",
        "student_name",
        "full name",
        "full_name",
        "st_name",
        "student",
        "اسم",
        "الاسم",
        "اسم الطالب"
    )

    fun parseFile(
        contentResolver: ContentResolver,
        uri: Uri,
        fileName: String?
    ): ImportParseResult {

        return try {

            val lowerName = fileName
                ?.lowercase(Locale.getDefault())
                ?: ""

            val rawRows = if (
                lowerName.endsWith(".csv") ||
                lowerName.endsWith(".txt")
            ) {
                contentResolver
                    .openInputStream(uri)
                    ?.use { parseTextFile(it) }
                    ?: emptyList()
            } else {
                contentResolver
                    .openInputStream(uri)
                    ?.use { parseExcelFile(it) }
                    ?: emptyList()
            }

            if (rawRows.isEmpty()) {
                return ImportParseResult.Error(
                    "The selected file is empty or could not be read."
                )
            }

            processRawRows(rawRows)

        } catch (e: Exception) {

            ImportParseResult.Error(
                "Failed to read the file: ${
                    e.localizedMessage ?: "Unknown error"
                }"
            )
        }
    }

    private fun parseExcelFile(
        inputStream: InputStream
    ): List<List<String>> {

        val workbook = WorkbookFactory.create(inputStream)

        return try {

            if (workbook.numberOfSheets == 0) {
                return emptyList()
            }

            val sheet = workbook.getSheetAt(0)
            val formatter = DataFormatter()

            val result = mutableListOf<List<String>>()

            for (row in sheet) {

                val maxCell =
                    row.lastCellNum.toInt()

                if (maxCell <= 0) {
                    continue
                }

                val rowList = mutableListOf<String>()
                var hasData = false

                for (index in 0 until maxCell) {

                    val cell = row.getCell(index)

                    val text =
                        if (cell != null) {
                            formatter
                                .formatCellValue(cell)
                                .trim()
                        } else {
                            ""
                        }

                    if (text.isNotEmpty()) {
                        hasData = true
                    }

                    rowList.add(text)
                }

                if (hasData) {
                    result.add(rowList)
                }
            }

            result

        } finally {
            workbook.close()
        }
    }

    private fun parseTextFile(
        inputStream: InputStream
    ): List<List<String>> {

        val result = mutableListOf<List<String>>()

        BufferedReader(
            InputStreamReader(inputStream)
        ).use { reader ->

            var line = reader.readLine()

            while (line != null) {

                val trimmed = line.trim()

                if (trimmed.isNotEmpty()) {

                    val delimiter = when {
                        trimmed.contains('\t') -> "\t"
                        trimmed.contains(',') -> ","
                        trimmed.contains(';') -> ";"
                        trimmed.contains('|') -> "|"
                        else -> "\\s+"
                    }

                    val parts = trimmed
                        .split(Regex(delimiter))
                        .map { it.trim() }

                    if (parts.any { it.isNotEmpty() }) {
                        result.add(parts)
                    }
                }

                line = reader.readLine()
            }
        }

        return result
    }

    private fun processRawRows(
        rawRows: List<List<String>>
    ): ImportParseResult {

        if (rawRows.isEmpty()) {
            return ImportParseResult.Error(
                "No data was found in the file."
            )
        }

        var headerRowIndex = -1
        var idColIndex = -1
        var nameColIndex = -1

        /*
         * Search the first few rows for recognizable
         * Student ID and Student Name headers.
         */
        for (
            rowIndex in 0 until minOf(15, rawRows.size)
        ) {

            val row = rawRows[rowIndex]

            var foundId = -1
            var foundName = -1

            for (columnIndex in row.indices) {

                val cell = normalize(
                    row[columnIndex]
                )

                if (
                    foundId == -1 &&
                    idKeywords.any {
                        matchesKeyword(cell, it)
                    }
                ) {
                    foundId = columnIndex
                }

                if (
                    foundName == -1 &&
                    nameKeywords.any {
                        matchesKeyword(cell, it)
                    }
                ) {
                    foundName = columnIndex
                }
            }

            if (
                foundId != -1 &&
                foundName != -1 &&
                foundId != foundName
            ) {
                headerRowIndex = rowIndex
                idColIndex = foundId
                nameColIndex = foundName
                break
            }
        }

        val maxColumns =
            rawRows.maxOfOrNull { it.size } ?: 0

        if (maxColumns == 0) {
            return ImportParseResult.Error(
                "No columns were found in the file."
            )
        }

        /*
         * If headers were detected, try importing directly.
         */
        if (
            headerRowIndex != -1 &&
            idColIndex != -1 &&
            nameColIndex != -1
        ) {

            val students = extractStudentsFromRows(
                rawRows = rawRows,
                startRowIndex = headerRowIndex + 1,
                idColIndex = idColIndex,
                nameColIndex = nameColIndex
            )

            if (students.isNotEmpty()) {
                return ImportParseResult.Success(
                    students = students
                )
            }
        }

        /*
         * Common case:
         * Two-column file with no recognizable headers.
         *
         * Try to determine which column is the ID
         * by checking whether its values contain digits.
         */
        if (
            maxColumns == 2 &&
            rawRows.isNotEmpty()
        ) {

            val sampleRows = rawRows
                .drop(
                    if (headerRowIndex >= 0) {
                        headerRowIndex + 1
                    } else {
                        0
                    }
                )
                .take(5)

            val firstColumnLooksNumeric =
                sampleRows.isNotEmpty() &&
                    sampleRows.all { row ->
                        row.getOrNull(0)
                            ?.any { it.isDigit() } == true
                    }

            val secondColumnLooksNumeric =
                sampleRows.isNotEmpty() &&
                    sampleRows.all { row ->
                        row.getOrNull(1)
                            ?.any { it.isDigit() } == true
                    }

            if (
                firstColumnLooksNumeric &&
                !secondColumnLooksNumeric
            ) {

                val students =
                    extractStudentsFromRows(
                        rawRows = rawRows,
                        startRowIndex =
                            if (headerRowIndex >= 0) {
                                headerRowIndex + 1
                            } else {
                                0
                            },
                        idColIndex = 0,
                        nameColIndex = 1
                    )

                if (students.isNotEmpty()) {
                    return ImportParseResult.Success(
                        students = students
                    )
                }
            }

            if (
                !firstColumnLooksNumeric &&
                secondColumnLooksNumeric
            ) {

                val students =
                    extractStudentsFromRows(
                        rawRows = rawRows,
                        startRowIndex =
                            if (headerRowIndex >= 0) {
                                headerRowIndex + 1
                            } else {
                                0
                            },
                        idColIndex = 1,
                        nameColIndex = 0
                    )

                if (students.isNotEmpty()) {
                    return ImportParseResult.Success(
                        students = students
                    )
                }
            }
        }

        /*
         * Couldn't determine the correct columns.
         * Ask the user to select them manually.
         */
        val columns =
            if (headerRowIndex >= 0) {
                rawRows[headerRowIndex]
            } else {
                (0 until maxColumns).map {
                    "Column ${it + 1}"
                }
            }

        return ImportParseResult.ColumnMappingRequired(
            columns = columns,
            rawRows = rawRows,
            detectedIdColIndex =
                if (idColIndex >= 0) {
                    idColIndex
                } else {
                    null
                },
            detectedNameColIndex =
                if (nameColIndex >= 0) {
                    nameColIndex
                } else {
                    null
                }
        )
    }

    fun extractStudentsFromRows(
        rawRows: List<List<String>>,
        startRowIndex: Int,
        idColIndex: Int,
        nameColIndex: Int
    ): List<RawStudent> {

        val students = mutableListOf<RawStudent>()

        if (rawRows.isEmpty()) {
            return students
        }

        val safeStartIndex =
            startRowIndex.coerceIn(
                0,
                rawRows.size
            )

        for (
            rowIndex in safeStartIndex until rawRows.size
        ) {

            val row = rawRows[rowIndex]

            val idValue =
                row.getOrNull(idColIndex)
                    ?.trim()
                    .orEmpty()

            val nameValue =
                row.getOrNull(nameColIndex)
                    ?.trim()
                    .orEmpty()

            /*
             * Ignore completely empty rows.
             */
            if (
                idValue.isEmpty() &&
                nameValue.isEmpty()
            ) {
                continue
            }

            /*
             * Ignore obvious header rows if they somehow
             * reach this method.
             */
            if (
                isHeaderValue(idValue, idKeywords) ||
                isHeaderValue(nameValue, nameKeywords)
            ) {
                continue
            }

            students.add(
                RawStudent(
                    studentId =
                        if (idValue.isNotEmpty()) {
                            idValue
                        } else {
                            "S${students.size + 1}"
                        },
                    studentName =
                        if (nameValue.isNotEmpty()) {
                            nameValue
                        } else {
                            "Student ${students.size + 1}"
                        }
                )
            )
        }

        return students
    }

    private fun normalize(
        value: String
    ): String {
        return value
            .trim()
            .lowercase(Locale.getDefault())
            .replace(
                Regex("[_\\-]+"),
                " "
            )
            .replace(
                Regex("\\s+"),
                " "
            )
    }

    private fun matchesKeyword(
        value: String,
        keyword: String
    ): Boolean {

        val normalizedKeyword =
            normalize(keyword)

        return value == normalizedKeyword ||
            value.contains(normalizedKeyword)
    }

    private fun isHeaderValue(
        value: String,
        keywords: List<String>
    ): Boolean {

        if (value.isBlank()) {
            return false
        }

        val normalizedValue =
            normalize(value)

        return keywords.any {
            normalizedValue == normalize(it)
        }
    }
}
