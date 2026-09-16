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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.ui.theme.DarkAmberWarning
import com.georgehany.quickattend.ui.theme.DarkEmeraldPresent
import com.georgehany.quickattend.ui.theme.DarkRedNotPresent
import com.georgehany.quickattend.ui.theme.EmeraldPresent
import com.georgehany.quickattend.ui.theme.RedNotPresent
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
    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    val displayedSessions = if (selectedTab == 0) {
        todaySessions
    } else {
        historySessions
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 24.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            HeaderSection()
        }

        item {
            AppearanceCard(
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
            Button(
                onClick = onStartOrCreateSessionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Start New Session",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }

        item {
            SessionTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        if (displayedSessions.isEmpty()) {

            item {
                EmptySessionsState(
                    isHistory = selectedTab == 1
                )
            }

        } else {

            items(
                items = displayedSessions,
                key = { session -> session.id }
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

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Student Attendance",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Manage your attendance sessions",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AppearanceCard(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (themeMode) {
                            ThemeMode.LIGHT -> Icons.Default.LightMode
                            ThemeMode.DARK -> Icons.Default.DarkMode
                            ThemeMode.SYSTEM -> Icons.Default.NightsStay
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = when (themeMode) {
                            ThemeMode.LIGHT -> "Light mode"
                            ThemeMode.DARK -> "Dark mode"
                            ThemeMode.SYSTEM -> "System default"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                ThemeOption(
                    modifier = Modifier.weight(1f),
                    text = "Light",
                    icon = Icons.Default.WbSunny,
                    selected = themeMode == ThemeMode.LIGHT,
                    onClick = {
                        onThemeModeChange(ThemeMode.LIGHT)
                    }
                )

                ThemeOption(
                    modifier = Modifier.weight(1f),
                    text = "Dark",
                    icon = Icons.Default.DarkMode,
                    selected = themeMode == ThemeMode.DARK,
                    onClick = {
                        onThemeModeChange(ThemeMode.DARK)
                    }
                )

                ThemeOption(
                    modifier = Modifier.weight(1f),
                    text = "System",
                    icon = Icons.Default.NightsStay,
                    selected = themeMode == ThemeMode.SYSTEM,
                    onClick = {
                        onThemeModeChange(ThemeMode.SYSTEM)
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(
    modifier: Modifier = Modifier,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = contentColor
            )

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                },
                color = contentColor
            )
        }
    }
}

@Composable
private fun OverviewCard(
    todaySessions: List<Session>,
    historySessions: List<Session>
) {
    val completedToday = todaySessions.count {
        it.status == "COMPLETED"
    }

    val inProgressToday = todaySessions.count {
        it.status == "IN_PROGRESS"
    }

    val totalSessions = historySessions.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OverviewItem(
                    modifier = Modifier.weight(1f),
                    value = todaySessions.size.toString(),
                    label = "Today",
                    icon = Icons.Default.Schedule
                )

                OverviewItem(
                    modifier = Modifier.weight(1f),
                    value = completedToday.toString(),
                    label = "Completed",
                    icon = Icons.Default.People
                )

                OverviewItem(
                    modifier = Modifier.weight(1f),
                    value = inProgressToday.toString(),
                    label = "In Progress",
                    icon = Icons.Default.Schedule
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "$totalSessions total sessions in history",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OverviewItem(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(19.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SessionTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {

        TabButton(
            modifier = Modifier.weight(1f),
            text = "Today",
            selected = selectedTab == 0,
            onClick = {
                onTabSelected(0)
            }
        )

        TabButton(
            modifier = Modifier.weight(1f),
            text = "History",
            selected = selectedTab == 1,
            onClick = {
                onTabSelected(1)
            }
        )
    }
}

@Composable
private fun TabButton(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(11.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(11.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.surface
        } else {
            Color.Transparent
        },
        shadowElevation = if (selected) 1.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                },
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun EmptySessionsState(
    isHistory: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = if (isHistory) {
                    "No attendance history"
                } else {
                    "No sessions today"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = if (isHistory) {
                    "Completed sessions will appear here."
                } else {
                    "Create a new session to start taking attendance."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SessionCard(
    session: Session,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val statusText = when (session.status) {
        "COMPLETED" -> "Completed"
        "IN_PROGRESS" -> "In Progress"
        else -> "Not Started"
    }

    val statusColor = when (session.status) {
        "COMPLETED" -> {
            if (MaterialTheme.colorScheme.background == Color(0xFF0F141C)) {
                DarkEmeraldPresent
            } else {
                EmeraldPresent
            }
        }

        "IN_PROGRESS" -> {
            if (MaterialTheme.colorScheme.background == Color(0xFF0F141C)) {
                DarkAmberWarning
            } else {
                Color(0xFFE89B2C)
            }
        }

        else -> {
            if (MaterialTheme.colorScheme.background == Color(0xFF0F141C)) {
                DarkRedNotPresent
            } else {
                RedNotPresent
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
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
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = session.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(8.dp)
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
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(
                        text = "${session.totalStudents} students",
                        style = MaterialTheme.typography.labelMedium,
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
    }
}
