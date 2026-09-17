package com.georgehany.quickattend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.ui.theme.ThemeMode

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
    var selectedTab by remember { mutableIntStateOf(0) }

    val completedToday = todaySessions.count {
        it.status == "COMPLETED"
    }

    val activeToday = todaySessions.count {
        it.status != "COMPLETED"
    }

    val studentsToday = todaySessions.sumOf {
        it.totalStudents
    }

    val sessionsToShow =
        if (selectedTab == 0) {
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
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 18.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ====================================================
            // HEADER
            // ====================================================

            item {
                HomeHeader(
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange
                )
            }

            // ====================================================
            // OVERVIEW
            // ====================================================

            item {
                OverviewCard(
                    sessions = todaySessions.size,
                    completed = completedToday,
                    active = activeToday,
                    students = studentsToday
                )
            }

            // ====================================================
            // CREATE SESSION
            // ====================================================

            item {
                CreateSessionCard(
                    onClick = onStartOrCreateSessionClick
                )
            }

            // ====================================================
            // SESSION SECTION
            // ====================================================

            item {
                Column {

                    Text(
                        text = "Attendance Sessions",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = if (selectedTab == 0) {
                            "Today's attendance activity"
                        } else {
                            "Previously recorded sessions"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ====================================================
            // TABS
            // ====================================================

            item {
                SessionTabs(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                    }
                )
            }

            // ====================================================
            // SESSIONS
            // ====================================================

            if (sessionsToShow.isEmpty()) {

                item {
                    EmptySessionsCard(
                        isHistory = selectedTab == 1,
                        onCreateSession = onStartOrCreateSessionClick
                    )
                }

            } else {

                items(
                    items = sessionsToShow,
                    key = { it.id }
                ) { session ->

                    SessionCard(
                        session = session,
                        onClick = {
                            onSessionSelect(session)
                        },
                        onDelete = {
                            onDeleteSession(session.id)
                        }
                    )
                }
            }
        }
    }
}

// ================================================================
// HEADER
// ================================================================

@Composable
private fun HomeHeader(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "EELU - Student Attendance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "Hello, Eng. George Hany",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }

        ThemeToggle(
            themeMode = themeMode,
            onThemeModeChange = onThemeModeChange
        )
    }
}

// ================================================================
// THEME TOGGLE
// ================================================================

@Composable
private fun ThemeToggle(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    Surface(
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {

        IconButton(
            onClick = {
                onThemeModeChange(
                    if (themeMode == ThemeMode.LIGHT) {
                        ThemeMode.DARK
                    } else {
                        ThemeMode.LIGHT
                    }
                )
            }
        ) {

            Icon(
                imageVector = if (themeMode == ThemeMode.LIGHT) {
                    Icons.Default.NightsStay
                } else {
                    Icons.Default.LightMode
                },
                contentDescription = if (themeMode == ThemeMode.LIGHT) {
                    "Switch to dark mode"
                } else {
                    "Switch to light mode"
                },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ================================================================
// OVERVIEW
// ================================================================

@Composable
private fun OverviewCard(
    sessions: Int,
    completed: Int,
    active: Int,
    students: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Today's attendance activity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = sessions.toString(),
                    label = "Sessions",
                    icon = Icons.Default.Schedule
                )

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = completed.toString(),
                    label = "Completed",
                    icon = Icons.Default.Check
                )

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = active.toString(),
                    label = "Active",
                    icon = Icons.Default.PlayArrow
                )

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = students.toString(),
                    label = "Students",
                    icon = Icons.Default.Groups
                )
            }
        }
    }
}

// ================================================================
// OVERVIEW METRIC
// ================================================================

@Composable
private fun OverviewMetric(
    modifier: Modifier,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 5.dp,
                    vertical = 11.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

// ================================================================
// CREATE SESSION CARD
// ================================================================

@Composable
private fun CreateSessionCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
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
                        MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.14f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(27.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Start New Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Import your student list and begin recording attendance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = 0.82f
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ================================================================
// TABS
// ================================================================

@Composable
private fun SessionTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {}
        ) {

            Tab(
                selected = selectedTab == 0,
                onClick = {
                    onTabSelected(0)
                },
                text = {
                    Text(
                        text = "Today",
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
                        text = "History",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    }
}

// ================================================================
// SESSION CARD
// ================================================================

@Composable
private fun SessionCard(
    session: Session,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = session.status == "COMPLETED"

    val completedStudents =
        session.currentIndex.coerceIn(
            0,
            session.totalStudents
        )

    val progress =
        if (session.totalStudents > 0) {
            completedStudents.toFloat() /
                    session.totalStudents.toFloat()
        } else {
            0f
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            if (isCompleted) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = if (isCompleted) {
                            Icons.Default.Check
                        } else {
                            Icons.Default.Schedule
                        },
                        contentDescription = null,
                        tint = if (isCompleted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                        modifier = Modifier.size(21.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = session.sectionName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "${session.date} • ${session.totalStudents} students",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
                modifier = Modifier.height(14.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    }
                ) {

                    Text(
                        text = if (isCompleted) {
                            "Completed"
                        } else {
                            "In Progress"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isCompleted) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        },
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        )
                    )
                }

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = if (isCompleted) {
                        "${session.totalStudents} / ${session.totalStudents}"
                    } else {
                        "$completedStudents / ${session.totalStudents}"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            androidx.compose.material3.LinearProgressIndicator(
                progress = {
                    progress.coerceIn(0f, 1f)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

// ================================================================
// EMPTY STATE
// ================================================================

@Composable
private fun EmptySessionsCard(
    isHistory: Boolean,
    onCreateSession: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = if (isHistory) {
                        Icons.Default.Schedule
                    } else {
                        Icons.Default.Today
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(27.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            Text(
                text = if (isHistory) {
                    "No Previous Sessions"
                } else {
                    "No Sessions Today"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = if (isHistory) {
                    "Completed attendance sessions will appear here."
                } else {
                    "Create an attendance session to get started."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!isHistory) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                OutlinedButton(
                    onClick = onCreateSession,
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )

                    Text(
                        text = "Create Session",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
