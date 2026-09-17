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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.georgehany.quickattend.data.local.entity.AttendanceRecord
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.data.local.entity.Student
import com.georgehany.quickattend.ui.theme.ErrorRed
import com.georgehany.quickattend.ui.theme.Success

@Composable
fun AttendanceScreen(
    session: Session,
    students: List<Student>,
    records: List<AttendanceRecord>,
    onRecordAttendance: (String, Boolean) -> Unit,
    onUndo: () -> Unit,
    onBack: () -> Unit
) {
    val currentStudent = students.getOrNull(session.currentIndex)

    val presentCount = records.count { it.isPresent }
    val absentCount = records.count { !it.isPresent }
    val remainingCount = (session.totalStudents - records.size).coerceAtLeast(0)

    val progress = if (session.totalStudents > 0) {
        (session.currentIndex.toFloat() / session.totalStudents.toFloat())
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AttendanceTopBar(
                session = session,
                canUndo = records.isNotEmpty(),
                onBack = onBack,
                onUndo = onUndo
            )
        }
    ) { paddingValues ->

        if (currentStudent == null) {
            EmptyAttendanceState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                ProgressCard(
                    current = session.currentIndex,
                    total = session.totalStudents,
                    progress = progress
                )

                AttendanceStats(
                    presentCount = presentCount,
                    absentCount = absentCount,
                    remainingCount = remainingCount
                )

                StudentCard(
                    student = currentStudent,
                    position = session.currentIndex + 1
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                AttendanceActions(
                    onPresent = {
                        onRecordAttendance(
                            currentStudent.studentId,
                            true
                        )
                    },
                    onAbsent = {
                        onRecordAttendance(
                            currentStudent.studentId,
                            false
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }
        }
    }
}

@Composable
private fun AttendanceTopBar(
    session: Session,
    canUndo: Boolean,
    onBack: () -> Unit,
    onUndo: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = session.sectionName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Student ${session.currentIndex + 1} of ${session.totalStudents}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onUndo,
                enabled = canUndo
            ) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = "Undo last attendance",
                    tint = if (canUndo) {
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
private fun ProgressCard(
    current: Int,
    total: Int,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Attendance Progress",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$current / $total",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun AttendanceStats(
    presentCount: Int,
    absentCount: Int,
    remainingCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AttendanceStat(
            modifier = Modifier.weight(1f),
            value = presentCount,
            label = "Present",
            icon = Icons.Default.Check,
            color = Success
        )

        AttendanceStat(
            modifier = Modifier.weight(1f),
            value = absentCount,
            label = "Absent",
            icon = Icons.Default.PersonOff,
            color = ErrorRed
        )

        AttendanceStat(
            modifier = Modifier.weight(1f),
            value = remainingCount,
            label = "Remaining",
            icon = Icons.Default.Person,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun AttendanceStat(
    modifier: Modifier,
    value: Int,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
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

@Composable
private fun StudentCard(
    student: Student,
    position: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Current Student",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = student.name.ifBlank {
                    "Student"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = student.studentId,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                    alpha = 0.75f
                )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface.copy(
                    alpha = 0.7f
                )
            ) {
                Text(
                    text = "Student #$position",
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 7.dp
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun AttendanceActions(
    onPresent: () -> Unit,
    onAbsent: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Button(
            onClick = onPresent,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Success,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = "PRESENT",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = onAbsent,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonOff,
                contentDescription = null,
                tint = ErrorRed
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = "NOT PRESENT",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ErrorRed
            )
        }
    }
}

@Composable
private fun EmptyAttendanceState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(52.dp)
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "No student available",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "There are no students left to record.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
