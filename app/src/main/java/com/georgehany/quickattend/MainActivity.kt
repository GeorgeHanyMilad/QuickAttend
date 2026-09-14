package com.georgehany.quickattend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.georgehany.quickattend.ui.screens.AttendanceScreen
import com.georgehany.quickattend.ui.screens.ColumnMappingDialog
import com.georgehany.quickattend.ui.screens.CreateSessionDialog
import com.georgehany.quickattend.ui.screens.HomeScreen
import com.georgehany.quickattend.ui.screens.SummaryScreen
import com.georgehany.quickattend.ui.theme.QuickAttendTheme
import com.georgehany.quickattend.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate()

        setContent {
            QuickAttendTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuickAttendMainContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun QuickAttendMainContent(viewModel: MainViewModel) {
    val context = LocalContext.current
    val todaySessions by viewModel.todaySessions.collectAsState()
    val historySessions by viewModel.historySessions.collectAsState()
    val activeSession by viewModel.activeSession.collectAsState()
    val activeStudents by viewModel.activeStudents.collectAsState()
    val activeRecords by viewModel.activeRecords.collectAsState()

    val columnMappingRequired by viewModel.columnMappingRequired.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUiMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        val currentSession = activeSession

        if (currentSession != null) {
            val isCompleted = currentSession.status == "COMPLETED" ||
                    (currentSession.currentIndex >= currentSession.totalStudents && currentSession.totalStudents > 0)

            if (isCompleted) {
                SummaryScreen(
                    session = currentSession,
                    students = activeStudents,
                    records = activeRecords,
                    onExportExcel = { viewModel.exportExcel(context, currentSession) },
                    onBack = { viewModel.closeActiveSession() }
                )
            } else {
                AttendanceScreen(
                    session = currentSession,
                    students = activeStudents,
                    records = activeRecords,
                    onRecordAttendance = { studentId, isPresent ->
                        viewModel.recordAttendance(studentId, isPresent)
                    },
                    onUndo = { viewModel.undoLastAttendance() },
                    onBack = { viewModel.closeActiveSession() }
                )
            }
        } else {
            HomeScreen(
                todaySessions = todaySessions,
                historySessions = historySessions,
                onStartOrCreateSessionClick = { showCreateDialog = true },
                onSessionSelect = { session ->
                    viewModel.startOrResumeSession(session)
                },
                onDeleteSession = { sessionId ->
                    viewModel.deleteSession(sessionId)
                }
            )
        }

        if (showCreateDialog) {
            CreateSessionDialog(
                onDismiss = { showCreateDialog = false },
                onFileSelected = { uri, fileName, sectionName ->
                    viewModel.onFileSelected(context, uri, fileName, sectionName)
                }
            )
        }

        columnMappingRequired?.let { mapping ->
            ColumnMappingDialog(
                mappingData = mapping,
                onConfirm = { idCol, nameCol ->
                    viewModel.confirmColumnMapping(idCol, nameCol)
                },
                onDismiss = { viewModel.dismissColumnMapping() }
            )
        }
    }
}
