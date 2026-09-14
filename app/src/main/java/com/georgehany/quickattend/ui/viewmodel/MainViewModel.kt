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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).attendanceDao()

    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
    val todayDateString: String = dateFormatter.format(Date())

    val todaySessions: StateFlow<List<Session>> = dao.getTodaySessions(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historySessions: StateFlow<List<Session>> = dao.getHistorySessions(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeSession = MutableStateFlow<Session?>(null)
    val activeSession: StateFlow<Session?> = _activeSession.asStateFlow()

    private val _activeStudents = MutableStateFlow<List<Student>>(emptyList())
    val activeStudents: StateFlow<List<Student>> = _activeStudents.asStateFlow()

    private val _activeRecords = MutableStateFlow<Map<String, AttendanceRecord>>(emptyMap())
    val activeRecords: StateFlow<Map<String, AttendanceRecord>> = _activeRecords.asStateFlow()

    private val _columnMappingRequired = MutableStateFlow<ImportParseResult.ColumnMappingRequired?>(null)
    val columnMappingRequired: StateFlow<ImportParseResult.ColumnMappingRequired?> = _columnMappingRequired.asStateFlow()

    private var pendingSectionName: String = ""

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun onFileSelected(context: Context, uri: Uri, fileName: String?, sectionName: String) {
        pendingSectionName = sectionName.ifBlank { "Section ${System.currentTimeMillis() % 100}" }
        viewModelScope.launch(Dispatchers.IO) {
            when (val parseResult = FileImporter.parseFile(context.contentResolver, uri, fileName)) {
                is ImportParseResult.Success -> {
                    createSessionFromRawStudents(pendingSectionName, parseResult.students)
                }
                is ImportParseResult.ColumnMappingRequired -> {
                    _columnMappingRequired.value = parseResult
                }
                is ImportParseResult.Error -> {
                    _uiMessage.value = parseResult.message
                }
            }
        }
    }

    fun confirmColumnMapping(idColIndex: Int, nameColIndex: Int) {
        val mapping = _columnMappingRequired.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val rawStudents = FileImporter.extractStudentsFromRows(
                mapping.rawRows,
                startRowIndex = 1,
                idColIndex = idColIndex,
                nameColIndex = nameColIndex
            )
            _columnMappingRequired.value = null
            if (rawStudents.isNotEmpty()) {
                createSessionFromRawStudents(pendingSectionName, rawStudents)
            } else {
                _uiMessage.value = "No valid student data found with selected columns."
            }
        }
    }

    fun dismissColumnMapping() {
        _columnMappingRequired.value = null
    }

    private suspend fun createSessionFromRawStudents(sectionName: String, rawStudents: List<RawStudent>) {
        val newSession = Session(
            sectionName = sectionName,
            date = todayDateString,
            totalStudents = rawStudents.size,
            status = "NOT_STARTED",
            currentIndex = 0
        )
        val students = rawStudents.mapIndexed { index, raw ->
            Student(
                sessionId = 0,
                studentId = raw.studentId,
                studentName = raw.studentName,
                sequenceOrder = index
            )
        }
        val sessionId = dao.createSessionWithStudents(newSession, students)
        _uiMessage.value = "Session for '$sectionName' created with ${students.size} students!"
    }

    fun startOrResumeSession(session: Session) {
        viewModelScope.launch(Dispatchers.IO) {
            var updatedSession = session
            val now = System.currentTimeMillis()

            if (session.startTimeMs == null) {
                updatedSession = session.copy(
                    startTimeMs = now,
                    status = if (session.currentIndex >= session.totalStudents && session.totalStudents > 0) "COMPLETED" else "IN_PROGRESS"
                )
                dao.updateSession(updatedSession)
            } else if (session.status == "NOT_STARTED") {
                updatedSession = session.copy(status = "IN_PROGRESS")
                dao.updateSession(updatedSession)
            }

            _activeSession.value = updatedSession
            loadSessionDetails(updatedSession.id)
        }
    }

    private suspend fun loadSessionDetails(sessionId: Long) {
        val students = dao.getStudentsForSession(sessionId)
        val recordsList = dao.getAttendanceRecordsForSession(sessionId)
        val recordsMap = recordsList.associateBy { it.studentId }

        _activeStudents.value = students
        _activeRecords.value = recordsMap
    }

    fun recordAttendance(studentId: String, isPresent: Boolean) {
        val currentSession = _activeSession.value ?: return
        val students = _activeStudents.value
        val currentIndex = currentSession.currentIndex

        if (currentIndex < 0 || currentIndex >= students.size) return

        viewModelScope.launch(Dispatchers.IO) {
            val record = AttendanceRecord(
                sessionId = currentSession.id,
                studentId = studentId,
                isPresent = isPresent,
                timestamp = System.currentTimeMillis()
            )
            dao.recordAttendance(record)

            val nextIndex = currentIndex + 1
            val isCompleted = nextIndex >= students.size
            val updatedSession = currentSession.copy(
                currentIndex = nextIndex,
                status = if (isCompleted) "COMPLETED" else "IN_PROGRESS",
                endTimeMs = if (isCompleted) System.currentTimeMillis() else currentSession.endTimeMs
            )

            dao.updateSession(updatedSession)
            _activeSession.value = updatedSession

            // Refresh records map
            val recordsList = dao.getAttendanceRecordsForSession(currentSession.id)
            _activeRecords.value = recordsList.associateBy { it.studentId }
        }
    }

    fun undoLastAttendance() {
        val currentSession = _activeSession.value ?: return
        val currentIndex = currentSession.currentIndex
        val students = _activeStudents.value

        if (currentIndex <= 0 || students.isEmpty()) return

        val prevIndex = currentIndex - 1
        val prevStudent = students.getOrNull(prevIndex) ?: return

        viewModelScope.launch(Dispatchers.IO) {
            // Delete previous attendance record to reset state
            dao.deleteAttendanceRecord(currentSession.id, prevStudent.studentId)

            val updatedSession = currentSession.copy(
                currentIndex = prevIndex,
                status = "IN_PROGRESS"
            )
            dao.updateSession(updatedSession)
            _activeSession.value = updatedSession

            // Refresh records map
            val recordsList = dao.getAttendanceRecordsForSession(currentSession.id)
            _activeRecords.value = recordsList.associateBy { it.studentId }
        }
    }

    fun exportExcel(context: Context, session: Session) {
        viewModelScope.launch(Dispatchers.IO) {
            val students = dao.getStudentsForSession(session.id)
            val records = dao.getAttendanceRecordsForSession(session.id)

            val file = ExcelExporter.exportSessionToExcel(context, session, students, records)
            withContext(Dispatchers.Main) {
                if (file != null) {
                    ExcelExporter.shareExportedFile(context, file, session.sectionName)
                } else {
                    _uiMessage.value = "Failed to export Excel file."
                }
            }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteSession(sessionId)
            if (_activeSession.value?.id == sessionId) {
                _activeSession.value = null
            }
        }
    }

    fun closeActiveSession() {
        _activeSession.value = null
        _activeStudents.value = emptyList()
        _activeRecords.value = emptyMap()
    }
}
