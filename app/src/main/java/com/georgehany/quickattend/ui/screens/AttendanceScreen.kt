```kotlin
package com.georgehany.quickattend.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Undo
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.georgehany.quickattend.data.local.entity.AttendanceRecord
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.data.local.entity.Student
import com.georgehany.quickattend.ui.theme.DarkAmberWarning
import com.georgehany.quickattend.ui.theme.DarkEmeraldPresent
import com.georgehany.quickattend.ui.theme.DarkRedNotPresent
import com.georgehany.quickattend.ui.theme.EmeraldPresent
import com.georgehany.quickattend.ui.theme.RedNotPresent

// ============================================================
// QuickAttend Attendance Screen
// ============================================================

@Composable
fun AttendanceScreen(
    session: Session,
    students: List<Student>,
    records: Map<String, AttendanceRecord>,
    onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit,
    onUndo: () -> Unit,
    onBack: () -> Unit
) {
    val totalStudents = session.totalStudents

    val currentIndex = session.currentIndex.coerceIn(
        0,
        (totalStudents - 1).coerceAtLeast(0)
    )

    val currentStudent = students.getOrNull(currentIndex)

    val presentCount =
        records.values.count { it.isPresent }

    val absentCount =
        records.values.count { !it.isPresent }

    val completedCount =
        presentCount + absentCount

    val progress =
        if (totalStudents > 0) {
            completedCount.toFloat() / totalStudents.toFloat()
        } else {
            0f
        }

    val progressPercent =
        (progress * 100).toInt()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AttendanceTopBar(
                sectionName = session.sectionName,
                currentIndex = currentIndex,
                totalStudents = totalStudents,
                onBack = onBack,
                onUndo = onUndo
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

            ProgressSection(
                currentIndex = currentIndex,
                totalStudents = totalStudents,
                progress = progress,
                progressPercent = progressPercent
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            AttendanceStats(
                presentCount = presentCount,
                absentCount = absentCount,
                totalStudents = totalStudents
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            if (currentStudent != null) {

                AnimatedContent(
                    targetState = currentStudent,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "StudentCardTransition"
                ) { student ->

                    CurrentStudentCard(
                        student = student,
                        currentIndex = currentIndex,
                        totalStudents = totalStudents
                    )
                }

            } else {

                AllStudentsCompletedCard()
            }

            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
            )

            AttendanceActions(
                enabled = currentStudent != null,
                onPresent = {
                    currentStudent?.let {
                        onRecordAttendance(
                            it.studentId,
                            true
                        )
                    }
                },
                onAbsent = {
                    currentStudent?.let {
                        onRecordAttendance(
                            it.studentId,
                            false
                        )
                    }
                },
                onUndo = onUndo,
                undoEnabled = currentIndex > 0
            )
        }
    }
}

// ============================================================
// TOP BAR
// ============================================================

@Composable
private fun AttendanceTopBar(
    sectionName: String,
    currentIndex: Int,
    totalStudents: Int,
    onBack: () -> Unit,
    onUndo: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 6.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
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

            if (totalStudents > 0) {

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 7.dp
                        )
                ) {

                    Text(
                        text =
                            "${currentIndex + 1}/$totalStudents",
                        style = MaterialTheme.typography.labelMedium,
                        color =
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (currentIndex > 0) {

                IconButton(
                    onClick = onUndo,
                    modifier = Modifier.size(44.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// ============================================================
// PROGRESS SECTION
// ============================================================

@Composable
private fun ProgressSection(
    currentIndex: Int,
    totalStudents: Int,
    progress: Float,
    progressPercent: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Attendance Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text =
                        if (totalStudents > 0) {
                            "Recording student ${currentIndex + 1} of $totalStudents"
                        } else {
                            "No students available"
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "$progressPercent%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(9.dp)
        )

        LinearProgressIndicator(
            progress = {
                progress.coerceIn(
                    0f,
                    1f
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(
                    RoundedCornerShape(10.dp)
                ),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

// ============================================================
// ATTENDANCE STATS
// ============================================================

@Composable
private fun AttendanceStats(
    presentCount: Int,
    absentCount: Int,
    totalStudents: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        AttendanceStatCard(
            modifier = Modifier.weight(1f),
            label = "Present",
            value = presentCount.toString(),
            icon = Icons.Default.Check,
            lightColor = EmeraldPresent,
            darkColor = DarkEmeraldPresent
        )

        AttendanceStatCard(
            modifier = Modifier.weight(1f),
            label = "Absent",
            value = absentCount.toString(),
            icon = Icons.Default.Close,
            lightColor = RedNotPresent,
            darkColor = DarkRedNotPresent
        )

        AttendanceStatCard(
            modifier = Modifier.weight(1f),
            label = "Total",
            value = totalStudents.toString(),
            icon = Icons.Default.Groups,
            lightColor = MaterialTheme.colorScheme.primary,
            darkColor = MaterialTheme.colorScheme.primary
        )
    }
}

// ============================================================
// STAT CARD
// ============================================================

@Composable
private fun AttendanceStatCard(
    modifier: Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    lightColor: Color,
    darkColor: Color
) {
    val isDark =
        MaterialTheme.colorScheme.background ==
                Color(0xFF0B1220)

    val accentColor =
        if (isDark) {
            darkColor
        } else {
            lightColor
        }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
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
                    horizontal = 8.dp,
                    vertical = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        accentColor.copy(
                            alpha = 0.12f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
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

// ============================================================
// CURRENT STUDENT
// ============================================================

@Composable
private fun CurrentStudentCard(
    student: Student,
    currentIndex: Int,
    totalStudents: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 28.dp
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
                    imageVector = Icons.Default.HowToReg,
                    contentDescription = null,
                    tint =
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "CURRENT STUDENT",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.3.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = student.studentName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {

                Text(
                    text = "ID  •  ${student.studentId}",
                    style = MaterialTheme.typography.labelLarge,
                    color =
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(
                        horizontal = 15.dp,
                        vertical = 8.dp
                    )
                )
            }

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            Text(
                text =
                    if (totalStudents > 0) {
                        "Student ${currentIndex + 1} of $totalStudents"
                    } else {
                        ""
                    },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================
// COMPLETED STATE
// ============================================================

@Composable
private fun AllStudentsCompletedCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(
                alpha = 0.18f
            )
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(
                            alpha = 0.75f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(35.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Text(
                text = "Attendance Completed",
                style = MaterialTheme.typography.headlineSmall,
                color =
                    MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "All students have been recorded successfully.",
                style = MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = 0.78f
                    ),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============================================================
// ATTENDANCE ACTIONS
// ============================================================

@Composable
private fun AttendanceActions(
    enabled: Boolean,
    onPresent: () -> Unit,
    onAbsent: () -> Unit,
    onUndo: () -> Unit,
    undoEnabled: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Button(
                onClick = onPresent,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPresent,
                    contentColor = Color.White,
                    disabledContainerColor =
                        MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(27.dp)
                )

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text = "Present",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onAbsent,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedNotPresent,
                    contentColor = Color.White,
                    disabledContainerColor =
                        MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(27.dp)
                )

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text = "Absent",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Button(
            onClick = onUndo,
            enabled = undoEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceVariant,
                contentColor =
                    MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor =
                    MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.55f
                    ),
                disabledContentColor =
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.45f
                    )
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp
            )
        ) {

            Icon(
                imageVector = Icons.Default.Undo,
                contentDescription = null,
                modifier = Modifier.size(19.dp)
            )

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            Text(
                text = "Undo Previous",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
```
