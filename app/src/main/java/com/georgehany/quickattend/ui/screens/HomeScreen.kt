package com.georgehany.quickattend.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
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
import com.georgehany.quickattend.ui.theme.AccentBlue
import com.georgehany.quickattend.ui.theme.AmberOnWarningContainer
import com.georgehany.quickattend.ui.theme.AmberWarningContainer
import com.georgehany.quickattend.ui.theme.EmeraldContainer
import com.georgehany.quickattend.ui.theme.EmeraldOnContainer
import com.georgehany.quickattend.ui.theme.NavyContainer
import com.georgehany.quickattend.ui.theme.NavyOnContainer
import com.georgehany.quickattend.ui.theme.NavyPrimary
import com.georgehany.quickattend.ui.theme.NeutralOnSurfaceSecondary
import com.georgehany.quickattend.ui.theme.NeutralOnSurfaceTertiary
import com.georgehany.quickattend.ui.theme.NeutralOutlineLight
import com.georgehany.quickattend.ui.theme.PureWhite

@Composable
fun HomeScreen(
    todaySessions: List<Session>,
    historySessions: List<Session>,
    onStartOrCreateSessionClick: () -> Unit,
    onSessionSelect: (Session) -> Unit,
    onDeleteSession: (Long) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val currentList = if (selectedTabIndex == 0) {
        todaySessions
    } else {
        historySessions
    }

    val totalStudentsToday = todaySessions.sumOf { it.totalStudents }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onStartOrCreateSessionClick,
                modifier = Modifier.padding(bottom = 4.dp),
                shape = RoundedCornerShape(18.dp),
                containerColor = NavyPrimary,
                contentColor = PureWhite
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create New Section"
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "New Section",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 18.dp,
                top = 18.dp,
                end = 18.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --------------------------------------------------
            // Header
            // --------------------------------------------------

            item {
                HomeHeader()
            }

            // --------------------------------------------------
            // Overview Card
            // --------------------------------------------------

            item {
                OverviewCard(
                    sessionsCount = todaySessions.size,
                    studentsCount = totalStudentsToday
                )
            }

            // --------------------------------------------------
            // Tabs
            // --------------------------------------------------

            item {
                Column {

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.Transparent,
                        contentColor = NavyPrimary,
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            selectedContentColor = NavyPrimary,
                            unselectedContentColor = NeutralOnSurfaceTertiary,
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(17.dp)
                                    )

                                    Spacer(modifier = Modifier.width(7.dp))

                                    Text(
                                        text = "Today",
                                        style = MaterialTheme.typography.labelLarge
                                    )

                                    Spacer(modifier = Modifier.width(5.dp))

                                    SessionCountBadge(
                                        count = todaySessions.size,
                                        selected = selectedTabIndex == 0
                                    )
                                }
                            }
                        )

                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            selectedContentColor = NavyPrimary,
                            unselectedContentColor = NeutralOnSurfaceTertiary,
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(17.dp)
                                    )

                                    Spacer(modifier = Modifier.width(7.dp))

                                    Text(
                                        text = "History",
                                        style = MaterialTheme.typography.labelLarge
                                    )

                                    Spacer(modifier = Modifier.width(5.dp))

                                    SessionCountBadge(
                                        count = historySessions.size,
                                        selected = selectedTabIndex == 1
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // --------------------------------------------------
            // Section title
            // --------------------------------------------------

            if (currentList.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (selectedTabIndex == 0) {
                                    "Today's Sessions"
                                } else {
                                    "Attendance History"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = if (selectedTabIndex == 0) {
                                    "Manage your attendance sessions"
                                } else {
                                    "Review previous attendance records"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = NeutralOnSurfaceSecondary
                            )
                        }
                    }
                }
            }

            // --------------------------------------------------
            // Sessions
            // --------------------------------------------------

            if (currentList.isEmpty()) {

                item {
                    EmptySessionsState(
                        isToday = selectedTabIndex == 0,
                        onCreateSession = onStartOrCreateSessionClick
                    )
                }

            } else {

                items(
                    items = currentList,
                    key = { it.id }
                ) { session ->

                    SessionItemCard(
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

// ============================================================
// Header
// ============================================================

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "QuickAttend",
                style = MaterialTheme.typography.headlineLarge,
                color = NavyPrimary
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Teaching Assistant Attendance Manager",
                style = MaterialTheme.typography.bodyMedium,
                color = NeutralOnSurfaceSecondary
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(NavyContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = "Attendance",
                tint = NavyOnContainer,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

// ============================================================
// Overview Card
// ============================================================

@Composable
private fun OverviewCard(
    sessionsCount: Int,
    studentsCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavyPrimary
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Today's Overview",
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (sessionsCount == 0) {
                            "No attendance sessions yet"
                        } else {
                            "Your attendance activity for today"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = PureWhite.copy(alpha = 0.72f)
                    )
                }

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PureWhite.copy(alpha = 0.9f),
                    modifier = Modifier.size(27.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OverviewMetric(
                    value = sessionsCount.toString(),
                    label = "Sessions",
                    modifier = Modifier.weight(1f)
                )

                OverviewMetric(
                    value = studentsCount.toString(),
                    label = "Students",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OverviewMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PureWhite.copy(alpha = 0.10f))
            .padding(
                horizontal = 14.dp,
                vertical = 12.dp
            )
    ) {
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = PureWhite
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.70f)
            )
        }
    }
}

// ============================================================
// Tab Badge
// ============================================================

@Composable
private fun SessionCountBadge(
    count: Int,
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (selected) {
                    NavyContainer
                } else {
                    NeutralOutlineLight
                }
            )
            .padding(
                horizontal = 7.dp,
                vertical = 2.dp
            )
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) {
                NavyOnContainer
            } else {
                NeutralOnSurfaceSecondary
            }
        )
    }
}

// ============================================================
// Session Card
// ============================================================

@Composable
fun SessionItemCard(
    session: Session,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = session.status == "COMPLETED"
    val isInProgress = session.status == "IN_PROGRESS"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
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
                .padding(17.dp)
        ) {

            // --------------------------------------------------
            // Top row
            // --------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            when {
                                isCompleted -> EmeraldContainer
                                isInProgress -> AmberWarningContainer
                                else -> NavyContainer
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isCompleted -> Icons.Default.CheckCircle
                            isInProgress -> Icons.Default.PlayArrow
                            else -> Icons.Default.CalendarToday
                        },
                        contentDescription = null,
                        tint = when {
                            isCompleted -> EmeraldOnContainer
                            isInProgress -> AmberOnWarningContainer
                            else -> NavyOnContainer
                        },
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = session.sectionName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${session.totalStudents} Students",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralOnSurfaceSecondary
                        )

                        Text(
                            text = "  •  ",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralOnSurfaceTertiary
                        )

                        Text(
                            text = session.date,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralOnSurfaceSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Session",
                        tint = NeutralOnSurfaceTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --------------------------------------------------
            // Status + Action
            // --------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                StatusBadge(
                    status = session.status,
                    currentIndex = session.currentIndex,
                    total = session.totalStudents
                )

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) {
                            EmeraldOnContainer
                        } else {
                            NavyPrimary
                        }
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 14.dp,
                        vertical = 9.dp
                    )
                ) {

                    Icon(
                        imageVector = if (isCompleted) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = when {
                            isCompleted -> "View Summary"
                            isInProgress -> "Continue"
                            else -> "Start"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.width(3.dp))

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ============================================================
// Status Badge
// ============================================================

@Composable
fun StatusBadge(
    status: String,
    currentIndex: Int,
    total: Int
) {
    val backgroundColor: Color
    val textColor: Color
    val text: String

    when (status) {

        "COMPLETED" -> {
            backgroundColor = EmeraldContainer
            textColor = EmeraldOnContainer
            text = "Completed"
        }

        "IN_PROGRESS" -> {
            backgroundColor = AmberWarningContainer
            textColor = AmberOnWarningContainer
            text = if (total > 0) {
                "In Progress  •  $currentIndex/$total"
            } else {
                "In Progress"
            }
        }

        else -> {
            backgroundColor = NeutralOutlineLight
            textColor = NeutralOnSurfaceSecondary
            text = "Not Started"
        }
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .padding(
                horizontal = 10.dp,
                vertical = 7.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(textColor)
        )

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

// ============================================================
// Empty State
// ============================================================

@Composable
private fun EmptySessionsState(
    isToday: Boolean,
    onCreateSession: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
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
                .padding(
                    horizontal = 24.dp,
                    vertical = 30.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(NavyContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isToday) {
                        Icons.Default.CalendarToday
                    } else {
                        Icons.Default.History
                    },
                    contentDescription = null,
                    tint = NavyOnContainer,
                    modifier = Modifier.size(29.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isToday) {
                    "Ready for a new session?"
                } else {
                    "No attendance history"
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isToday) {
                    "Upload your student list and start taking attendance."
                } else {
                    "Completed attendance sessions will appear here."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = NeutralOnSurfaceSecondary
            )

            if (isToday) {
                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onCreateSession,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Create Session",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
