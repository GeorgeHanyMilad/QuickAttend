package com.georgehany.quickattend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.ui.theme.DarkSuccess
import com.georgehany.quickattend.ui.theme.ErrorRed
import com.georgehany.quickattend.ui.theme.Success
import com.georgehany.quickattend.ui.theme.ThemeMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    todaySessions: List<Session>,
    historySessions: List<Session>,
    onStartOrCreateSessionClick: () -> Unit,
    onSessionSelect: (Session) -> Unit,
    onDeleteSession: (Long) -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var sessionToDelete by remember {
        mutableStateOf<Session?>(null)
    }

    val displayedSessions = if (selectedTab == 0) {
        todaySessions
    } else {
        historySessions
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                HomeHeader(
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange
                )
            }

            item {
                OverviewCard(
                    todaySessions = todaySessions,
                    historySessions = historySessions
                )
            }

            item {
                StartAttendanceCard(
                    onClick = onStartOrCreateSessionClick
                )
            }

            item {
                SessionTabs(
                    selectedTab = selectedTab,
                    todayCount = todaySessions.size,
                    historyCount = historySessions.size,
                    onTabSelected = {
                        selectedTab = it
                    }
                )
            }

            if (displayedSessions.isEmpty()) {

                item {
                    EmptySessionsState(
                        isToday = selectedTab == 0,
                        onCreateSession = onStartOrCreateSessionClick
                    )
                }

            } else {

                items(
                    items = displayedSessions,
                    key = { it.id }
                ) { session ->

                    SessionCard(
                        session = session,
                        onClick = {
                            onSessionSelect(session)
                        },
                        onDelete = {
                            sessionToDelete = session
                        }
                    )
                }
            }
        }
    }

    sessionToDelete?.let { session ->

        DeleteSessionDialog(
            session = session,
            onDismiss = {
                sessionToDelete = null
            },
            onConfirm = {
                onDeleteSession(session.id)
                sessionToDelete = null
            }
        )
    }
}

@Composable
private fun HomeHeader(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "EELU - Student Attendance",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Hello, Eng. George Hany",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        ThemeToggle(
            themeMode = themeMode,
            onThemeModeChange = onThemeModeChange
        )
    }
}

@Composable
private fun ThemeToggle(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    onThemeModeChange(ThemeMode.LIGHT)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LightMode,
                    contentDescription = "Light mode",
                    tint = if (themeMode == ThemeMode.LIGHT) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            IconButton(
                onClick = {
                    onThemeModeChange(ThemeMode.DARK)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = "Dark mode",
                    tint = if (themeMode == ThemeMode.DARK) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun OverviewCard(
    todaySessions: List<Session>,
    historySessions: List<Session>
) {
    val allSessions = todaySessions + historySessions

    val completedCount = allSessions.count {
        it.status == "COMPLETED"
    }

    val activeCount = allSessions.count {
        it.status == "IN_PROGRESS"
    }

    val totalStudents = allSessions.sumOf {
        it.totalStudents
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Attendance activity at a glance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.78f
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.TaskAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = allSessions.size.toString(),
                    label = "Sessions"
                )

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = completedCount.toString(),
                    label = "Completed"
                )

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = activeCount.toString(),
                    label = "Active"
                )

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = totalStudents.toString(),
                    label = "Students"
                )
            }
        }
    }
}

@Composable
private fun OverviewMetric(
    modifier: Modifier,
    value: String,
    label: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.12f)
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 12.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(
                    alpha = 0.72f
                )
            )
        }
    }
}

@Composable
private fun StartAttendanceCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Start New Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Import your student list and begin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SessionTabs(
    selectedTab: Int,
    todayCount: Int,
    historyCount: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary
    ) {

        Tab(
            selected = selectedTab == 0,
            onClick = {
                onTabSelected(0)
            },
            text = {
                Text(
                    text = "Today ($todayCount)",
                    fontWeight = FontWeight.SemiBold
                )
            }
        )

        Tab(
            selected = selectedTab == 1,
            onClick = {
                onTabSelected(1)
            },
            text = {
                Text(
                    text = "History ($historyCount)",
                    fontWeight = FontWeight.SemiBold
                )
            }
        )
    }
}

@Composable
private fun SessionCard(
    session: Session,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = session.status == "COMPLETED"
    val isActive = session.status == "IN_PROGRESS"

    val progress = if (session.totalStudents > 0) {
        (
            session.currentIndex.toFloat() /
                session.totalStudents.toFloat()
            ).coerceIn(0f, 1f)
    } else {
        0f
    }

    val statusText = when {
        isCompleted -> "Completed"
        isActive -> "In Progress"
        else -> "Not Started"
    }

    val statusColor = when {
        isCompleted -> Success
        isActive -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = if (isCompleted) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.Groups
                        },
                        contentDescription = null,
                        tint = if (isCompleted) {
                            Success
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.size(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = session.sectionName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.size(5.dp)
                        )

                        Text(
                            text = formatSessionDate(session.date),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete session",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "${
                        session.currentIndex
                    } / ${
                        session.totalStudents
                    } students",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            androidx.compose.material3.LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = if (isCompleted) {
                    Success
                } else {
                    MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun EmptySessionsState(
    isToday: Boolean,
    onCreateSession: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = if (isToday) {
                        Icons.Default.Schedule
                    } else {
                        Icons.Default.CalendarToday
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = if (isToday) {
                    "No attendance sessions today"
                } else {
                    "No attendance history"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = if (isToday) {
                    "Create a session to start recording student attendance."
                } else {
                    "Completed and previous sessions will appear here."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isToday) {

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Button(
                    onClick = onCreateSession,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.size(8.dp)
                    )

                    Text(
                        text = "Create Session"
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteSessionDialog(
    session: Session,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        icon = {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = null,
                tint = ErrorRed
            )
        },

        title = {
            Text(
                text = "Delete Session?"
            )
        },

        text = {
            Text(
                text = "This will permanently delete the attendance session for \"${session.sectionName}\" and all its records."
            )
        },

        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Delete"
                )
            }
        },

        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Cancel"
                )
            }
        }
    )
}

private fun formatSessionDate(
    date: String
): String {
    return try {
        val input = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        val output = SimpleDateFormat(
            "EEE, dd MMM yyyy",
            Locale.getDefault()
        )

        input.parse(date)?.let {
            output.format(it)
        } ?: date

    } catch (_: Exception) {
        date
    }
}
