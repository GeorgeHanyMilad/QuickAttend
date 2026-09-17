package com.georgehany.quickattend.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.georgehany.quickattend.data.local.entity.AttendanceRecord
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.data.local.entity.Student
import com.georgehany.quickattend.ui.theme.ErrorRed
import com.georgehany.quickattend.ui.theme.Success

@Composable
fun SummaryScreen(
    session: Session,
    students: List<Student>,
    records: List<AttendanceRecord>,
    onExportExcel: () -> Unit,
    onBack: () -> Unit
) {
    val totalStudents = students.size.coerceAtLeast(session.totalStudents)

    val presentCount = records.count { it.isPresent }
    val absentCount = records.count { !it.isPresent }

    val attendanceRate = if (totalStudents > 0) {
        (presentCount.toFloat() / totalStudents.toFloat()) * 100f
    } else {
        0f
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SummaryTopBar(
                sectionName = session.sectionName,
                onBack = onBack
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                horizontal = 18.dp,
                vertical = 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ====================================================
            // COMPLETION HEADER
            // ====================================================

            item {
                CompletionCard(
                    attendanceRate = attendanceRate
                )
            }

            // ====================================================
            // OVERVIEW
            // ====================================================

            item {
                SummaryOverviewCard(
                    totalStudents = totalStudents,
                    present = presentCount,
                    absent = absentCount,
                    attendanceRate = attendanceRate
                )
            }

            // ====================================================
            // SESSION DETAILS
            // ====================================================

            item {
                SessionDetailsCard(
                    session = session,
                    totalStudents = totalStudents
                )
            }

            // ====================================================
            // EXPORT
            // ====================================================

            item {
                ExportCard(
                    onExportExcel = onExportExcel
                )
            }

            // ====================================================
            // STUDENT BREAKDOWN
            // ====================================================

            item {
                SectionTitle(
                    title = "Student Breakdown",
                    subtitle = "Individual attendance results"
                )
            }

            if (students.isEmpty()) {

                item {
                    EmptyStudentBreakdown()
                }

            } else {

                items(
                    items = students,
                    key = { it.id }
                ) { student ->

                    val record = records.firstOrNull {
                        it.studentId == student.studentId
                    }

                    StudentSummaryRow(
                        student = student,
                        isPresent = record?.isPresent
                    )
                }
            }
        }
    }
}

// ================================================================
// TOP BAR
// ================================================================

@Composable
private fun SummaryTopBar(
    sectionName: String,
    onBack: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = sectionName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Attendance Summary",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ================================================================
// COMPLETION CARD
// ================================================================

@Composable
private fun CompletionCard(
    attendanceRate: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
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
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.14f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Attendance Completed",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "The attendance session has been successfully recorded.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = 0.82f
                    )
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = "${attendanceRate.toInt()}%",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "attendance",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = 0.75f
                    )
                )
            }
        }
    }
}

// ================================================================
// OVERVIEW
// ================================================================

@Composable
private fun SummaryOverviewCard(
    totalStudents: Int,
    present: Int,
    absent: Int,
    attendanceRate: Float
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
                .padding(18.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(21.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(11.dp)
                )

                Column {

                    Text(
                        text = "Attendance Overview",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Session performance at a glance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                SummaryMetric(
                    modifier = Modifier.weight(1f),
                    value = totalStudents.toString(),
                    label = "Students",
                    icon = Icons.Default.Groups,
                    tint = MaterialTheme.colorScheme.primary
                )

                SummaryMetric(
                    modifier = Modifier.weight(1f),
                    value = present.toString(),
                    label = "Present",
                    icon = Icons.Default.Check,
                    tint = Success
                )

                SummaryMetric(
                    modifier = Modifier.weight(1f),
                    value = absent.toString(),
                    label = "Absent",
                    icon = Icons.Default.Close,
                    tint = ErrorRed
                )

                SummaryMetric(
                    modifier = Modifier.weight(1f),
                    value = "${attendanceRate.toInt()}%",
                    label = "Rate",
                    icon = Icons.Default.Percent,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Attendance rate",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${attendanceRate.toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            LinearProgressIndicator(
                progress = {
                    (attendanceRate / 100f).coerceIn(0f, 1f)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = Success,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

// ================================================================
// METRIC
// ================================================================

@Composable
private fun SummaryMetric(
    modifier: Modifier,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
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
                    horizontal = 4.dp,
                    vertical = 11.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ================================================================
// SESSION DETAILS
// ================================================================

@Composable
private fun SessionDetailsCard(
    session: Session,
    totalStudents: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                .padding(18.dp)
        ) {

            Text(
                text = "Session Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            SummaryDetailRow(
                icon = Icons.Default.Groups,
                label = "Section",
                value = session.sectionName
            )

            SummaryDetailRow(
                icon = Icons.Default.CalendarToday,
                label = "Date",
                value = session.date
            )

            SummaryDetailRow(
                icon = Icons.Default.Groups,
                label = "Students",
                value = totalStudents.toString()
            )

            SummaryDetailRow(
                icon = Icons.Default.Schedule,
                label = "Status",
                value = "Completed",
                valueColor = Success
            )
        }
    }
}

// ================================================================
// DETAIL ROW
// ================================================================

@Composable
private fun SummaryDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(19.dp)
        )

        Spacer(
            modifier = Modifier.width(11.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ================================================================
// EXPORT
// ================================================================

@Composable
private fun ExportCard(
    onExportExcel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
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
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Export Attendance",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Save the session as an Excel report.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onExportExcel,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(
                    horizontal = 14.dp,
                    vertical = 9.dp
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "Excel",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ================================================================
// SECTION TITLE
// ================================================================

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {
    Column {

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ================================================================
// STUDENT ROW
// ================================================================

@Composable
private fun StudentSummaryRow(
    student: Student,
    isPresent: Boolean?
) {
    val statusColor = when (isPresent) {
        true -> Success
        false -> ErrorRed
        null -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val statusText = when (isPresent) {
        true -> "Present"
        false -> "Absent"
        null -> "Not Recorded"
    }

    val statusIcon = when (isPresent) {
        true -> Icons.Default.Check
        false -> Icons.Default.Close
        null -> Icons.Default.Schedule
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = student.name
                        .trim()
                        .firstOrNull()
                        ?.uppercase()
                        ?: "?",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.width(11.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = student.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = student.studentId,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                shape = RoundedCornerShape(9.dp),
                color = statusColor.copy(alpha = 0.11f)
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 6.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(15.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ================================================================
// EMPTY STUDENT LIST
// ================================================================

@Composable
private fun EmptyStudentBreakdown() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(30.dp)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "No student records available.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
