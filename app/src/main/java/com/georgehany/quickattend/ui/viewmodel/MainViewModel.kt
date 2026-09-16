package com.georgehany.quickattend.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.georgehany.quickattend.data.export.ExcelExporter
import com.georgehany.quickattend.data.local.AppDatabase
import com.georgehany.quickattend.data.local.AttendanceRecord
import com.georgehany.quickattend.data.local.Session
import com.georgehany.quickattend.data.local.Student
import com.georgehany.quickattend.data.parser.ColumnMappingData
import com.georgehany.quickattend.data.parser.FileImporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    private val dateFormatter = SimpleDateFormat(
        "dd MMMM yyyy",
        Locale.ENGLISH
    )

    /*
     * ---------------------------------------------------------
     * Sessions
     * ---------------------------------------------------------
     */

    val todaySessions: StateFlow<List<Session>> =
        sessionDao.getAllSessions()
            .map { sessions ->
                val today = dateFormatter.format(Date())

                sessions.filter { session ->
                    session.date == today
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val historySessions: StateFlow<List<Session>> =
        sessionDao.getAllSessions()
            .map { sessions ->
                val today = dateFormatter.format(Date())

                sessions.filter { session ->
                    session.date != today
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    /*
     * ---------------------------------------------------------
     * Active Session
     * ---------------------------------------------------------
     */

    private val _activeSession =
        MutableStateFlow<Session?>(null)

    val activeSession: StateFlow<Session?> =
        _activeSession.asStateFlow()

    private val _activeStudents =
        MutableStateFlow<List<Student>>(emptyList())

    val activeStudents: StateFlow<List<Student>> =
        _activeStudents.asStateFlow()

    private val _activeRecords =
        MutableStateFlow<List<AttendanceRecord>>(emptyList())

    val activeRecords: StateFlow<List<AttendanceRecord>> =
        _activeRecords.asStateFlow()

    /*
     * ---------------------------------------------------------
     * Column Mapping
     * ---------------------------------------------------------
     */

    private val _columnMappingRequired =
        MutableStateFlow<ColumnMappingData?>(null)

    val columnMappingRequired: StateFlow<ColumnMappingData?> =
        _columnMappingRequired.asStateFlow()

    private var pendingUri: Uri? = null

    private var pendingSectionName: String? = null

    /*
     * ---------------------------------------------------------
     * UI Messages
     * ---------------------------------------------------------
     */

    private val _uiMessage =
        MutableStateFlow<String?>(null)

    val uiMessage: StateFlow<String?> =
        _uiMessage.asStateFlow()

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    private fun showMessage(message: String) {
        _uiMessage.value = message
    }

    /*
     * ---------------------------------------------------------
     * File Import
     * ---------------------------------------------------------
     */

    fun onFileSelected(
        context: Context,
        uri: Uri,
        fileName: String,
        sectionName: String
    ) {
        viewModelScope.launch {

            try {

                val result = FileImporter.parseFile(
                    context = context,
                    uri = uri,
                    fileName = fileName
                )

                when (result) {

                    is FileImporter.ParseResult.Success -> {

                        createSessionFromRawStudents(
                            sectionName = sectionName,
                            students = result.students
                        )
                    }

                    is FileImporter.ParseResult.MappingRequired -> {

                        pendingUri = uri
                        pendingSectionName = sectionName

                        _columnMappingRequired.value =
                            result.mappingData
                    }

                    is FileImporter.ParseResult.Error -> {

                        showMessage(
                            result.message
                        )
                    }
                }

            } catch (e: Exception) {

                showMessage(
                    e.message
                        ?: "Unable to import the selected file."
                )
            }
        }
    }

    /*
     * ---------------------------------------------------------
     * Confirm Column Mapping
     * ---------------------------------------------------------
     */

    fun confirmColumnMapping(
        idColumn: Int,
        nameColumn: Int
    ) {
        val uri = pendingUri
        val sectionName = pendingSectionName

        if (uri == null || sectionName.isNullOrBlank()) {
            showMessage(
                "Unable to continue. Please import the file again."
            )
            return
        }

        viewModelScope.launch {

            try {

                val context = getApplication<Application>()

                val result =
                    FileImporter.parseFileWithColumnMapping(
                        context = context,
                        uri = uri,
                        idColumn = idColumn,
                        nameColumn = nameColumn
                    )

                when (result) {

                    is FileImporter.ParseResult.Success -> {

                        createSessionFromRawStudents(
                            sectionName = sectionName,
                            students = result.students
                        )

                        clearPendingMapping()
                    }

                    is FileImporter.ParseResult.Error -> {

                        showMessage(
                            result.message
                        )
                    }

                    is FileImporter.ParseResult.MappingRequired -> {

                        showMessage(
                            "Please select valid student ID and name columns."
                        )
                    }
                }

            } catch (e: Exception) {

                showMessage(
                    e.message
                        ?: "Unable to process the selected columns."
                )
            }
        }
    }

    fun dismissColumnMapping() {
        clearPendingMapping()
    }

    private fun clearPendingMapping() {
        pendingUri = null
        pendingSectionName = null
        _columnMappingRequired.value = null
    }

    /*
     * ---------------------------------------------------------
     * Create Session
     * ---------------------------------------------------------
     */

    private suspend fun createSessionFromRawStudents(
        sectionName: String,
        students: List<Student>
    ) {
        if (students.isEmpty()) {
            showMessage(
                "No students were found in the selected file."
            )
            return
        }

        val currentDate =
            dateFormatter.format(Date())

        val session = Session(
            sectionName = sectionName,
            date = currentDate,
            totalStudents = students.size,
            currentIndex = 0,
            status = "NOT_STARTED",
            startTimeMs = null,
            endTimeMs = null
        )

        database.runInTransaction {

            val sessionId =
                sessionDao.insertSession(session)

            val studentsWithSession =
                students.map { student ->
                    student.copy(
                        sessionId = sessionId
                    )
                }

            studentDao.insertStudents(
                studentsWithSession
            )
        }

        showMessage(
            "Session created successfully."
        )
    }

    /*
     * ---------------------------------------------------------
     * Start / Resume Session
     * ---------------------------------------------------------
     */

    fun startOrResumeSession(
        session: Session
    ) {
        viewModelScope.launch {

            var updatedSession = session

            if (session.status == "NOT_STARTED") {

                updatedSession = session.copy(
                    status = "IN_PROGRESS",
                    startTimeMs = System.currentTimeMillis()
                )

                sessionDao.updateSession(
                    updatedSession
                )
            }

            _activeSession.value = updatedSession

            loadActiveSessionData(
                updatedSession
            )
        }
    }

    private suspend fun loadActiveSessionData(
        session: Session
    ) {
        _activeStudents.value =
            studentDao.getStudentsForSession(
                session.id
            )

        _activeRecords.value =
            attendanceDao.getRecordsForSession(
                session.id
            )
    }

    /*
     * ---------------------------------------------------------
     * Record Attendance
     * ---------------------------------------------------------
     */

    fun recordAttendance(
        studentId: Long,
        isPresent: Boolean
    ) {
        val session = _activeSession.value
            ?: return

        viewModelScope.launch {

            val record = AttendanceRecord(
                sessionId = session.id,
                studentId = studentId,
                isPresent = isPresent,
                timestamp = System.currentTimeMillis()
            )

            attendanceDao.insertRecord(
                record
            )

            val newIndex =
                session.currentIndex + 1

            val isCompleted =
                newIndex >= session.totalStudents &&
                session.totalStudents > 0

            val updatedSession =
                session.copy(
                    currentIndex = newIndex,
                    status = if (isCompleted) {
                        "COMPLETED"
                    } else {
                        "IN_PROGRESS"
                    },
                    endTimeMs = if (isCompleted) {
                        System.currentTimeMillis()
                    } else {
                        null
                    }
                )

            sessionDao.updateSession(
                updatedSession
            )

            _activeSession.value =
                updatedSession

            _activeRecords.value =
                attendanceDao.getRecordsForSession(
                    session.id
                )
        }
    }

    /*
     * ---------------------------------------------------------
     * Undo Last Attendance
     * ---------------------------------------------------------
     */

    fun undoLastAttendance() {
        val session = _activeSession.value
            ?: return

        if (session.currentIndex <= 0) {
            showMessage(
                "There is no attendance record to undo."
            )
            return
        }

        viewModelScope.launch {

            val records =
                attendanceDao.getRecordsForSession(
                    session.id
                )

            val lastRecord =
                records.lastOrNull()

            if (lastRecord == null) {
                showMessage(
                    "There is no attendance record to undo."
                )
                return@launch
            }

            attendanceDao.deleteRecord(
                lastRecord
            )

            val updatedSession =
                session.copy(
                    currentIndex =
                        (session.currentIndex - 1)
                            .coerceAtLeast(0),
                    status = "IN_PROGRESS",
                    endTimeMs = null
                )

            sessionDao.updateSession(
                updatedSession
            )

            _activeSession.value =
                updatedSession

            _activeRecords.value =
                attendanceDao.getRecordsForSession(
                    session.id
                )

            showMessage(
                "Last attendance record was undone."
            )
        }
    }

    /*
     * ---------------------------------------------------------
     * Excel Export
     * ---------------------------------------------------------
     */

    fun exportExcel(
        context: Context,
        session: Session
    ) {
        viewModelScope.launch {

            try {

                val students =
                    studentDao.getStudentsForSession(
                        session.id
                    )

                val records =
                    attendanceDao.getRecordsForSession(
                        session.id
                    )

                val fileUri =
                    ExcelExporter.exportSession(
                        context = context,
                        session = session,
                        students = students,
                        records = records
                    )

                ExcelExporter.shareExcelFile(
                    context = context,
                    fileUri = fileUri
                )

                showMessage(
                    "Attendance Excel file is ready to share."
                )

            } catch (e: Exception) {

                showMessage(
                    e.message
                        ?: "Unable to export attendance."
                )
            }
        }
    }

    /*
     * ---------------------------------------------------------
     * Delete Session
     * ---------------------------------------------------------
     */

    fun deleteSession(
        sessionId: Long
    ) {
        viewModelScope.launch {

            try {

                val session =
                    sessionDao.getSessionById(
                        sessionId
                    )

                if (session != null) {
                    database.runInTransaction {

                        attendanceDao.deleteRecordsForSession(
                            sessionId
                        )

                        studentDao.deleteStudentsForSession(
                            sessionId
                        )

                        sessionDao.deleteSession(
                            session
                        )
                    }

                    if (_activeSession.value?.id == sessionId) {
                        closeActiveSession()
                    }

                    showMessage(
                        "Session deleted successfully."
                    )
                }

            } catch (e: Exception) {

                showMessage(
                    e.message
                        ?: "Unable to delete the session."
                )
            }
        }
    }

    /*
     * ---------------------------------------------------------
     * Close Active Session
     * ---------------------------------------------------------
     */

    fun closeActiveSession() {
        _activeSession.value = null
        _activeStudents.value = emptyList()
        _activeRecords.value = emptyList()
    }
}
