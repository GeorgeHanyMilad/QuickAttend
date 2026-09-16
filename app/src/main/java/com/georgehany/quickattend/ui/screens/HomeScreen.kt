```kotlin
package com.georgehany.quickattend.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.ui.theme.DarkAmberWarning
import com.georgehany.quickattend.ui.theme.DarkEmeraldPresent
import com.georgehany.quickattend.ui.theme.DarkRedNotPresent
import com.georgehany.quickattend.ui.theme.EmeraldPresent
import com.georgehany.quickattend.ui.theme.RedNotPresent
import com.georgehany.quickattend.ui.theme.ThemeMode

// ============================================================
// QuickAttend Home Screen
// ============================================================

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

    val displayedSessions =
        if (selectedTab == 0) {
            todaySessions
        } else {
            historySessions
        }

    val completedToday =
        todaySessions.count {
            it.status == "COMPLETED"
        }

    val inProgressToday =
        todaySessions.count {
            it.status == "IN_PROGRESS"
        }

    val totalStudentsToday =
        todaySessions.sumOf {
            it.totalStudents
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 22.dp,
            bottom = 36.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ----------------------------------------------------
        // Header
        // ----------------------------------------------------

        item {
            HomeHeader(
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange
            )
        }

        // ----------------------------------------------------
        // Main Dashboard
        // ----------------------------------------------------

        item {
            DashboardCard(
                todaySessions = todaySessions.size,
                completedToday = completedToday,
                inProgressToday = inProgressToday,
                totalStudentsToday = totalStudentsToday
            )
        }

        // ----------------------------------------------------
        // Create Session
        // ----------------------------------------------------

        item {
            CreateSessionCard(
                onClick = onStartOrCreateSessionClick
            )
        }

        // ----------------------------------------------------
        // Sessions Header
        // ----------------------------------------------------

        item {
            SessionsHeader(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                }
            )
        }

        // ----------------------------------------------------
        // Session List
        // ----------------------------------------------------

        if (displayedSessions.isEmpty()) {

            item {
                EmptySessionsState(
                    isHistory = selectedTab == 1,
                    onCreateSession = onStartOrCreateSessionClick
                )
            }

        } else {

            items(
                items = displayedSessions,
                key = {
                    session -> session.id
                }
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

// ============================================================
// HEADER
// ============================================================

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
                text = "Hello, Eng. George Hany",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Manage your attendance sessions with ease.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        ThemeToggle(
            themeMode = themeMode,
            onThemeModeChange = onThemeModeChange
        )
    }
}

// ============================================================
// THEME TOGGLE
// ============================================================

@Composable
private fun ThemeToggle(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    val isDark =
        themeMode == ThemeMode.DARK

    Surface(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .clickable {
                onThemeModeChange(
                    if (isDark) {
                        ThemeMode.LIGHT
                    } else {
                        ThemeMode.DARK
                    }
                )
            },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector =
                    if (isDark) {
                        Icons.Default.LightMode
                    } else {
                        Icons.Default.DarkMode
                    },
                contentDescription =
                    if (isDark) {
                        "Switch to light mode"
                    } else {
                        "Switch to dark mode"
                    },
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(21.dp)
            )
        }
    }
}

// ============================================================
// DASHBOARD
// ============================================================

@Composable
private fun DashboardCard(
    todaySessions: Int,
    completedToday: Int,
    inProgressToday: Int,
    totalStudentsToday: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Attendance Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Your teaching activity at a glance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.75f
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.onPrimary.copy(
                                alpha = 0.12f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                DashboardMetric(
                    modifier = Modifier.weight(1f),
                    value = todaySessions.toString(),
                    label = "Sessions",
                    icon = Icons.Default.Schedule
                )

                DashboardMetric(
                    modifier = Modifier.weight(1f),
                    value = completedToday.toString(),
                    label = "Completed",
                    icon = Icons.Default.CheckCircle
                )

                DashboardMetric(
                    modifier = Modifier.weight(1f),
                    value = inProgressToday.toString(),
                    label = "Active",
                    icon = Icons.Default.PendingActions
                )

                DashboardMetric(
                    modifier = Modifier.weight(1f),
                    value = totalStudentsToday.toString(),
                    label = "Students",
                    icon = Icons.Default.Groups
                )
            }
        }
    }
}

// ============================================================
// DASHBOARD METRIC
// ============================================================

@Composable
private fun DashboardMetric(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(15.dp)
            )
            .background(
                MaterialTheme.colorScheme.onPrimary.copy(
                    alpha = 0.10f
                )
            )
            .padding(
                horizontal = 5.dp,
                vertical = 11.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary.copy(
                alpha = 0.82f
            ),
            modifier = Modifier.size(17.dp)
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(
                alpha = 0.72f
            ),
            maxLines = 1
        )
    }
}

// ============================================================
// CREATE SESSION CARD
// ============================================================

@Composable
private fun CreateSessionCard(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(20.dp)
            )
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(
                        RoundedCornerShape(15.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(13.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Create New Session",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Import a student list and start attendance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ============================================================
// SESSIONS HEADER + TABS
// ============================================================

@Composable
private fun SessionsHeader(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Attendance Sessions",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text =
                        if (selectedTab == 0) {
                            "Today's sessions"
                        } else {
                            "Previous sessions"
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector =
                    if (selectedTab == 0) {
                        Icons.Default.Schedule
                    } else {
                        Icons.Default.History
                    },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(14.dp)
                )
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(4.dp)
        ) {

            SessionTab(
                modifier = Modifier.weight(1f),
                text = "Today",
                selected = selectedTab == 0,
                onClick = {
                    onTabSelected(0)
                }
            )

            SessionTab(
                modifier = Modifier.weight(1f),
                text = "History",
                selected = selectedTab == 1,
                onClick = {
                    onTabSelected(1)
                }
            )
        }
    }
}

@Composable
private fun SessionTab(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(
                RoundedCornerShape(11.dp)
            )
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(11.dp),
        color =
            if (selected) {
                MaterialTheme.colorScheme.surface
            } else {
                Color.Transparent
            },
        shadowElevation =
            if (selected) {
                1.dp
            } else {
                0.dp
            }
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color =
                    if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                fontWeight =
                    if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    }
            )
        }
    }
}

// ============================================================
// SESSION CARD
// ============================================================

@Composable
private fun SessionCard(
    session: Session,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isDark =
        MaterialTheme.colorScheme.background ==
                Color(0xFF0B1220)

    val statusText =
        when (session.status) {
            "COMPLETED" -> "Completed"
            "IN_PROGRESS" -> "In Progress"
            else -> "Not Started"
        }

    val statusColor =
        when (session.status) {

            "COMPLETED" ->
                if (isDark) {
                    DarkEmeraldPresent
                } else {
                    EmeraldPresent
                }

            "IN_PROGRESS" ->
                if (isDark) {
                    DarkAmberWarning
                } else {
                    Color(0xFFE89B2C)
                }

            else ->
                if (isDark) {
                    DarkRedNotPresent
                } else {
                    RedNotPresent
                }
        }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(20.dp)
            )
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        top = 15.dp,
                        end = 7.dp,
                        bottom = 15.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(23.dp)
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
                        modifier = Modifier.height(4.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = session.date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                "${session.totalStudents} students",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelMedium,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete session",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(21.dp)
                    )
                }
            }

            if (session.status == "IN_PROGRESS") {

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.PendingActions,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )

                    Text(
                        text = "Continue attendance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text =
                            "${session.currentIndex}/${session.totalStudents}",
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ============================================================
// EMPTY STATE
// ============================================================

@Composable
private fun EmptySessionsState(
    isHistory: Boolean,
    onCreateSession: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 34.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector =
                        if (isHistory) {
                            Icons.Default.History
                        } else {
                            Icons.Default.Schedule
                        },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(29.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Text(
                text =
                    if (isHistory) {
                        "No Attendance History"
                    } else {
                        "No Sessions Today"
                    },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    if (isHistory) {
                        "Completed attendance sessions will appear here."
                    } else {
                        "Create a new session to start taking attendance."
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (!isHistory) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                TextButton(
                    onClick = onCreateSession
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = "Create Session",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
```
