package com.georgehany.quickattend.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.georgehany.quickattend.data.export.ExcelExporter
import com.georgehany.quickattend.data.local.AppDatabase
import com.georgehany.quickattend.data.local.entity.AttendanceRecord
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.data.local.entity.Student
import com.georgehany.quickattend.data.parser.FileImporter
import com.georgehany.quickattend.data.parser.ImportParseResult
import com.georgehany.quickattend.data.parser.RawStudent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)

    private val sessionDao = database.sessionDao()
    private val studentDao = database.studentDao()
    private val attendanceDao = database.attendanceDao()

    private val _todaySessions = MutableStateFlow<List<Session>>(emptyList())
    val todaySessions: StateFlow<List<Session>> = _todaySessions.asStateFlow()

    private val _historySessions = MutableStateFlow<List<Session>>(emptyList())
    val historySessions: StateFlow<List<Session>> = _historySessions.asStateFlow()

    private val _activeSession = MutableStateFlow<Session?>(null)
    val activeSession: StateFlow<Session?> = _activeSession.asStateFlow()

    private val _activeStudents = MutableStateFlow<List<Student>>(emptyList())
    val activeStudents: StateFlow<List<Student>> = _activeStudents.asStateFlow()

    private val _activeRecords = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val activeRecords: StateFlow<List<AttendanceRecord>> = _activeRecords.asStateFlow()

    private val _columnMappingRequired =
        MutableStateFlow<ImportParseResult.ColumnMappingRequired?>(null)

    val columnMappingRequired: StateFlow<ImportParseResult.ColumnMappingRequired?> =
        _columnMappingRequired.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private var pendingUri: Uri? = null
    private var pendingSectionName: String? = null

    init {
        observeSessions()
    }

    private fun observeSessions() {
        viewModelScope.launch {
            sessionDao.getAllSessions().collect { sessions ->
                val today = getTodayDate()

                _todaySessions.value = sessions.filter {
                    it.date == today
                }

                _historySessions.value = sessions.filter {
                    it.date != today
                }
            }
        }
    }

    fun onFileSelected(
        context: Context,
        uri: Uri,
        fileName: String?,
        sectionName: String
    ) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                FileImporter.parseFile(
                    contentResolver = context.contentResolver,
                    uri = uri,
                    fileName = fileName
                )
            }

            when (result) {

                is ImportParseResult.Success -> {
                    if (result.students.isEmpty()) {
                        _uiMessage.value = "No students were found in the selected file."
                        return@launch
                    }

                    createSessionFromRawStudents(
                        sectionName = sectionName,
                        students = result.students
                    )
                }

                is ImportParseResult.ColumnMappingRequired -> {
                    pendingUri = uri
                    pendingSectionName = sectionName
                    _columnMappingRequired.value = result
                }

                is ImportParseResult.Error -> {
                    _uiMessage.value = result.message
                }
            }
        }
    }

    fun confirmColumnMapping(
        idColumn: Int,
        nameColumn: Int
    ) {
        val mapping = _columnMappingRequired.value
        val sectionName = pendingSectionName

        if (mapping == null || sectionName.isNullOrBlank()) {
            _uiMessage.value = "Import information is no longer available."
            return
        }

        viewModelScope.launch {
            val students = withContext(Dispatchers.Default) {
                FileImporter.extractStudentsFromRows(
                    rawRows = mapping.rawRows,
                    startRowIndex = determineDataStartRow(mapping),
                    idColIndex = idColumn,
                    nameColIndex = nameColumn
                )
            }

            if (students.isEmpty()) {
                _uiMessage.value = "No valid students were found using these columns."
                return@launch
            }

            _columnMappingRequired.value = null
            pendingUri = null
            pendingSectionName = null

            createSessionFromRawStudents(
                sectionName = sectionName,
                students = students
            )
        }
    }

    fun dismissColumnMapping() {
        _columnMappingRequired.value = null
        pendingUri = null
        pendingSectionName = null
    }

    private fun determineDataStartRow(
        mapping: ImportParseResult.ColumnMappingRequired
    ): Int {
        val firstRow = mapping.rawRows.firstOrNull()

        if (firstRow != null) {
            val idIndex = mapping.detectedIdColIndex
            val nameIndex = mapping.detectedNameColIndex

            if (idIndex != null && nameIndex != null) {
                val idHeader = firstRow.getOrNull(idIndex)?.lowercase()?.trim()
                val nameHeader = firstRow.getOrNull(nameIndex)?.lowercase()?.trim()

                val hasHeaderWords =
                    idHeader != null &&
                    nameHeader != null &&
                    (
                        idHeader.contains("id") ||
                            idHeader.contains("code") ||
                            idHeader.contains("number") ||
                            idHeader.contains("رقم") ||
                            idHeader.contains("كود")
                        ) &&
                    (
                        nameHeader.contains("name") ||
                            nameHeader.contains("student") ||
                            nameHeader.contains("اسم")
                        )

                if (hasHeaderWords) {
                    return 1
                }
            }
        }

        return 0
    }

    private fun createSessionFromRawStudents(
        sectionName: String,
        students: List<RawStudent>
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val cleanStudents = students
                .filter {
                    it.studentId.isNotBlank() ||
                        it.studentName.isNotBlank()
                }
                .distinctBy {
                    it.studentId.trim()
                }

            if (cleanStudents.isEmpty()) {
                withContext(Dispatchers.Main) {
                    _uiMessage.value = "No valid students were found."
                }
                return@launch
            }

            val session = Session(
                sectionName = sectionName.trim(),
                date = getTodayDate(),
                totalStudents = cleanStudents.size,
                currentIndex = 0,
                status = "NOT_STARTED",
                startTimeMs = null,
                endTimeMs = null
            )

            val sessionId = sessionDao.insertSession(session)

            val studentEntities = cleanStudents.map {
                Student(
                    sessionId = sessionId,
                    studentId = it.studentId.trim(),
                    name = it.studentName.trim()
                )
            }

            studentDao.insertStudents(studentEntities)

            val createdSession = session.copy(
                id = sessionId
            )

            withContext(Dispatchers.Main) {
                startOrResumeSession(createdSession)
            }
        }
    }

    fun startOrResumeSession(session: Session) {
        viewModelScope.launch(Dispatchers.IO) {

            val latestSession =
                sessionDao.getSessionById(session.id) ?: session

            val students =
                studentDao.getStudentsForSession(latestSession.id)

            val records =
                attendanceDao.getRecordsForSession(latestSession.id)

            val updatedSession = when {
                latestSession.status == "NOT_STARTED" &&
                    students.isNotEmpty() -> {

                    val startedSession = latestSession.copy(
                        status = "IN_PROGRESS",
                        startTimeMs = latestSession.startTimeMs
                            ?: System.currentTimeMillis()
                    )

                    sessionDao.updateSession(startedSession)
                    startedSession
                }

                else -> latestSession
            }

            withContext(Dispatchers.Main) {
                _activeSession.value = updatedSession
                _activeStudents.value = students
                _activeRecords.value = records
            }
        }
    }

    fun recordAttendance(
        studentId: String,
        isPresent: Boolean
    ) {
        val session = _activeSession.value ?: return

        if (session.currentIndex >= session.totalStudents) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {

            val record = AttendanceRecord(
                sessionId = session.id,
                studentId = studentId,
                isPresent = isPresent,
                timestamp = System.currentTimeMillis()
            )

            attendanceDao.insertRecord(record)

            val newIndex = session.currentIndex + 1
            val isCompleted = newIndex >= session.totalStudents

            val updatedSession = session.copy(
                currentIndex = newIndex,
                status = if (isCompleted) {
                    "COMPLETED"
                } else {
                    "IN_PROGRESS"
                },
                endTimeMs = if (isCompleted) {
                    System.currentTimeMillis()
                } else {
                    session.endTimeMs
                }
            )

            sessionDao.updateSession(updatedSession)

            val updatedRecords =
                attendanceDao.getRecordsForSession(session.id)

            withContext(Dispatchers.Main) {
                _activeSession.value = updatedSession
                _activeRecords.value = updatedRecords
            }
        }
    }

    fun undoLastAttendance() {
        val session = _activeSession.value ?: return

        viewModelScope.launch(Dispatchers.IO) {

            val records =
                attendanceDao.getRecordsForSession(session.id)

            val lastRecord = records.lastOrNull()

            if (lastRecord == null) {
                withContext(Dispatchers.Main) {
                    _uiMessage.value = "There is no attendance record to undo."
                }
                return@launch
            }

            attendanceDao.deleteRecord(lastRecord)

            val newIndex =
                (session.currentIndex - 1).coerceAtLeast(0)

            val updatedSession = session.copy(
                currentIndex = newIndex,
                status = if (newIndex == 0) {
                    "IN_PROGRESS"
                } else {
                    "IN_PROGRESS"
                },
                endTimeMs = null
            )

            sessionDao.updateSession(updatedSession)

            val updatedRecords =
                attendanceDao.getRecordsForSession(session.id)

            withContext(Dispatchers.Main) {
                _activeSession.value = updatedSession
                _activeRecords.value = updatedRecords
            }
        }
    }

    fun closeActiveSession() {
        _activeSession.value = null
        _activeStudents.value = emptyList()
        _activeRecords.value = emptyList()
    }

    fun deleteSession(
        sessionId: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val session =
                sessionDao.getSessionById(sessionId)

            if (session == null) {
                withContext(Dispatchers.Main) {
                    _uiMessage.value = "Session not found."
                }
                return@launch
            }

            attendanceDao.deleteRecordsForSession(sessionId)
            studentDao.deleteStudentsForSession(sessionId)
            sessionDao.deleteSession(session)

            withContext(Dispatchers.Main) {
                if (_activeSession.value?.id == sessionId) {
                    closeActiveSession()
                }

                _uiMessage.value = "Session deleted successfully."
            }
        }
    }

    fun exportExcel(
        context: Context,
        session: Session
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val students =
                    studentDao.getStudentsForSession(session.id)

                val records =
                    attendanceDao.getRecordsForSession(session.id)

                val file =
                    ExcelExporter.exportSessionToExcel(
                        context = context,
                        session = session,
                        students = students,
                        records = records
                    )

                withContext(Dispatchers.Main) {
                    if (file != null) {
                        ExcelExporter.shareExportedFile(
                            context = context,
                            file = file,
                            sectionName = session.sectionName
                        )
                    } else {
                        _uiMessage.value =
                            "Failed to create the Excel file."
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiMessage.value =
                        "Export failed: ${
                            e.localizedMessage ?: "Unknown error"
                        }"
                }
            }
        }
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())
    }
}
