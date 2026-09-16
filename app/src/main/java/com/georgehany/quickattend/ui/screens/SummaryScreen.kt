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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.georgehany.quickattend.data.local.entity.AttendanceRecord
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.data.local.entity.Student
import com.georgehany.quickattend.ui.theme.EmeraldContainer
import com.georgehany.quickattend.ui.theme.EmeraldOnContainer
import com.georgehany.quickattend.ui.theme.EmeraldPresent
import com.georgehany.quickattend.ui.theme.NavyContainer
import com.georgehany.quickattend.ui.theme.NavyOnContainer
import com.georgehany.quickattend.ui.theme.NavyPrimary
import com.georgehany.quickattend.ui.theme.NeutralOnSurfaceSecondary
import com.georgehany.quickattend.ui.theme.NeutralOnSurfaceTertiary
import com.georgehany.quickattend.ui.theme.NeutralVariant
import com.georgehany.quickattend.ui.theme.RedContainer
import com.georgehany.quickattend.ui.theme.RedNotPresent
import com.georgehany.quickattend.ui.theme.RedOnContainer
import com.georgehany.quickattend.ui.theme.PureWhite

// ============================================================
// Summary Screen
// ============================================================

@Composable
fun SummaryScreen(
    session: Session,
    students: List<Student>,
    records: Map<String, AttendanceRecord>,
    onExportExcel: () -> Unit,
    onBack: () -> Unit
) {
    val totalStudents = students.size

    val presentCount = students.count { student ->
        records[student.studentId]?.isPresent == true
    }

    val absentCount = totalStudents - presentCount

    val attendanceRate = if (totalStudents > 0) {
        presentCount.toFloat() / totalStudents.toFloat()
    } else {
        0f
    }

    val attendancePercent = (attendanceRate * 100).toInt()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SummaryTopBar(
                sectionName = session.sectionName,
                onBack = onBack
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                )
        ) {

            // --------------------------------------------------
            // Completion Header
            // --------------------------------------------------

            CompletionHeader(
                attendancePercent = attendancePercent
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --------------------------------------------------
            // Overview
            // --------------------------------------------------

            AttendanceOverviewCard(
                presentCount = presentCount,
                absentCount = absentCount,
                totalStudents = totalStudents,
                attendanceRate = attendanceRate,
                attendancePercent = attendancePercent
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --------------------------------------------------
            // Session Details
            // --------------------------------------------------

            SessionDetailsCard(
                session = session
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --------------------------------------------------
            // Export
            // --------------------------------------------------

            Button(
                onClick = onExportExcel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyPrimary,
                    contentColor = PureWhite
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(21.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Export Attendance to Excel",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // --------------------------------------------------
            // Student List Header
            // --------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Student Breakdown",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "$totalStudents students",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralOnSurfaceSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NavyContainer)
                        .padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                ) {
                    Text(
                        text = "$attendancePercent%",
                        style = MaterialTheme.typography.labelLarge,
                        color = NavyOnContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --------------------------------------------------
            // Student List
            // --------------------------------------------------

            if (students.isEmpty()) {

                EmptySummaryState()

            } else {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    students.forEach { student ->

                        StudentSummaryRow(
                            student = student,
                            record = records[student.studentId]
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// Top Bar
// ============================================================

@Composable
private fun SummaryTopBar(
    sectionName: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyPrimary)
            .padding(
                start = 8.dp,
                end = 12.dp,
                top = 10.dp,
                bottom = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onBack,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = PureWhite
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Attendance Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = sectionName,
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================
// Completion Header
// ============================================================

@Composable
private fun CompletionHeader(
    attendancePercent: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(EmeraldContainer)
            .padding(
                horizontal = 16.dp,
                vertical = 15.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(PureWhite.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = EmeraldPresent,
                modifier = Modifier.size(25.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Attendance Completed",
                style = MaterialTheme.typography.titleMedium,
                color = EmeraldOnContainer
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "The session has been fully recorded.",
                style = MaterialTheme.typography.bodySmall,
                color = EmeraldOnContainer.copy(alpha = 0.78f)
            )
        }

        Text(
            text = "$attendancePercent%",
            style = MaterialTheme.typography.titleLarge,
            color = EmeraldOnContainer
        )
    }
}

// ============================================================
// Attendance Overview
// ============================================================

@Composable
private fun AttendanceOverviewCard(
    presentCount: Int,
    absentCount: Int,
    totalStudents: Int,
    attendanceRate: Float,
    attendancePercent: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = PureWhite
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

            Text(
                text = "Attendance Overview",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                SummaryMetricBox(
                    label = "Present",
                    value = presentCount.toString(),
                    icon = Icons.Default.Check,
                    backgroundColor = EmeraldContainer,
                    contentColor = EmeraldOnContainer,
                    modifier = Modifier.weight(1f)
                )

                SummaryMetricBox(
                    label = "Absent",
                    value = absentCount.toString(),
                    icon = Icons.Default.Close,
                    backgroundColor = RedContainer,
                    contentColor = RedOnContainer,
                    modifier = Modifier.weight(1f)
                )

                SummaryMetricBox(
                    label = "Total",
                    value = totalStudents.toString(),
                    icon = Icons.Default.Groups,
                    backgroundColor = NavyContainer,
                    contentColor = NavyOnContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Attendance Rate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralOnSurfaceSecondary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "$attendancePercent%",
                    style = MaterialTheme.typography.titleMedium,
                    color = NavyPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = {
                    attendanceRate.coerceIn(0f, 1f)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = NavyPrimary,
                trackColor = NeutralVariant
            )
        }
    }
}

// ============================================================
// Summary Metric Box
// ============================================================

@Composable
fun SummaryMetricBox(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(backgroundColor)
            .padding(
                horizontal = 10.dp,
                vertical = 12.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = contentColor
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor.copy(alpha = 0.8f)
        )
    }
}

// ============================================================
// Session Details
// ============================================================

@Composable
private fun SessionDetailsCard(
    session: Session
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PureWhite
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

            Text(
                text = "Session Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryDetailRow(
                label = "Section",
                value = session.sectionName
            )

            SummaryDetailRow(
                label = "Date",
                value = session.date
            )

            SummaryDetailRow(
                label = "Students",
                value = session.totalStudents.toString()
            )

            SummaryDetailRow(
                label = "Status",
                value = "Completed"
            )
        }
    }
}

// ============================================================
// Detail Row
// ============================================================

@Composable
private fun SummaryDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = NeutralOnSurfaceTertiary,
            modifier = Modifier.width(90.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

// ============================================================
// Student Summary Row
// ============================================================

@Composable
private fun StudentSummaryRow(
    student: Student,
    record: AttendanceRecord?
) {
    val isPresent = record?.isPresent == true

    val backgroundColor = if (isPresent) {
        EmeraldContainer
    } else {
        RedContainer
    }

    val contentColor = if (isPresent) {
        EmeraldOnContainer
    } else {
        RedOnContainer
    }

    val statusText = if (isPresent) {
        "Present"
    } else {
        "Absent"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(backgroundColor)
            .padding(
                horizontal = 13.dp,
                vertical = 11.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PureWhite.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = if (isPresent) {
                    Icons.Default.Check
                } else {
                    Icons.Default.Close
                },
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = student.studentName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = student.studentId,
                style = MaterialTheme.typography.bodySmall,
                color = NeutralOnSurfaceSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = statusText,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor
        )
    }
}

// ============================================================
// Empty State
// ============================================================

@Composable
private fun EmptySummaryState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "No students found.",
            style = MaterialTheme.typography.bodyMedium,
            color = NeutralOnSurfaceSecondary
        )
    }
}
