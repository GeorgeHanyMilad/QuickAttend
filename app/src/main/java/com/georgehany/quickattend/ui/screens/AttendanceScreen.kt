package com.georgehany.quickattend.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val currentIndex = session.currentIndex.coerceIn(
        0,
        (students.size - 1).coerceAtLeast(0)
    )

    val currentStudent = students.getOrNull(currentIndex)

    val presentCount = records.count { it.isPresent }
    val absentCount = records.count { !it.isPresent }
    val recordedCount = records.size

    val totalStudents = students.size.coerceAtLeast(session.totalStudents)

    val progress = if (totalStudents > 0) {
        recordedCount.toFloat() / totalStudents.toFloat()
    } else {
        0f
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AttendanceTopBar(
                sectionName = session.sectionName,
                currentIndex = currentIndex,
                totalStudents = totalStudents,
                onBack = onBack,
                onUndo = onUndo,
                canUndo = records.isNotEmpty()
            )
        }
    ) { paddingValues ->

        if (currentStudent == null) {
            EmptyAttendanceState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onBack = onBack
            )
        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                AttendanceProgress(
                    currentIndex = currentIndex,
                    totalStudents = totalStudents,
                    progress = progress
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                AttendanceStats(
                    present = presentCount,
                    absent = absentCount,
                    remaining = (totalStudents - recordedCount)
                        .coerceAtLeast(0)
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                CurrentStudentCard(
                    student = currentStudent,
                    currentNumber = currentIndex + 1,
                    totalStudents = totalStudents
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
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Select the student's attendance status",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ================================================================
// TOP BAR
// ================================================================

@Composable
private fun AttendanceTopBar(
    sectionName: String,
    currentIndex: Int,
    totalStudents: Int,
    onBack: () -> Unit,
    onUndo: () -> Unit,
    canUndo: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
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

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Attendance Session",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {

                Text(
                    text = if (totalStudents > 0) {
                        "${(currentIndex + 1).coerceAtMost(totalStudents)} / $totalStudents"
                    } else {
                        "0 / 0"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 7.dp
                    )
                )
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            IconButton(
                onClick = onUndo,
                enabled = canUndo
            ) {

                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Undo last attendance",
                    tint = if (canUndo) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.4f
                        )
                    }
                )
            }
        }
    }
}

// ================================================================
// PROGRESS
// ================================================================

@Composable
private fun AttendanceProgress(
    currentIndex: Int,
    totalStudents: Int,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
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

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Attendance Progress",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "Student ${currentIndex + 1} of $totalStudents",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            LinearProgressIndicator(
                progress = {
                    progress.coerceIn(0f, 1f)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

// ================================================================
// STATS
// ================================================================

@Composable
private fun AttendanceStats(
    present: Int,
    absent: Int,
    remaining: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {

        AttendanceStat(
            modifier = Modifier.weight(1f),
            value = present.toString(),
            label = "Present",
            icon = Icons.Default.Check,
            iconTint = Success
        )

        AttendanceStat(
            modifier = Modifier.weight(1f),
            value = absent.toString(),
            label = "Absent",
            icon = Icons.Default.Close,
            iconTint = ErrorRed
        )

        AttendanceStat(
            modifier = Modifier.weight(1f),
            value = remaining.toString(),
            label = "Remaining",
            icon = Icons.Default.Schedule,
            iconTint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun AttendanceStat(
    modifier: Modifier,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 11.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        iconTint.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            Column {

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
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
}

// ================================================================
// CURRENT STUDENT
// ================================================================

@Composable
private fun CurrentStudentCard(
    student: Student,
    currentNumber: Int,
    totalStudents: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 22.dp,
                    vertical = 26.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Current Student",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            AnimatedContent(
                targetState = student.name,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "student_name"
            ) { name ->

                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Surface(
                shape = RoundedCornerShape(9.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                Text(
                    text = student.studentId,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        horizontal = 11.dp,
                        vertical = 6.dp
                    )
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Student $currentNumber of $totalStudents",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ================================================================
// ATTENDANCE ACTIONS
// ================================================================

@Composable
private fun AttendanceActions(
    onPresent: () -> Unit,
    onAbsent: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Button(
            onClick = onPresent,
            modifier = Modifier
                .weight(1f)
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Success,
                contentColor = androidx.compose.ui.graphics.Color.White
            )
        ) {

            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Present",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onAbsent,
            modifier = Modifier
                .weight(1f)
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRed,
                contentColor = androidx.compose.ui.graphics.Color.White
            )
        ) {

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Absent",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ================================================================
// EMPTY STATE
// ================================================================

@Composable
private fun EmptyAttendanceState(
    modifier: Modifier,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "No Students Available",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "There are no students available for this attendance session.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Go Back",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
