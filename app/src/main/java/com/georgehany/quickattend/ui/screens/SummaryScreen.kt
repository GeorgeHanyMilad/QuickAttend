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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
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
import com.georgehany.quickattend.ui.theme.AmberWarningContainer
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

    val presentCount = records.values.count { it.isPresent }

    val notPresentCount = records.values.count { !it.isPresent }

    val completedCount = presentCount + notPresentCount

    val progress = if (totalStudents > 0) {
        completedCount.toFloat() / totalStudents.toFloat()
    } else {
        0f
    }

    val progressPercent = (progress * 100).toInt()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AttendanceTopBar(
                sectionName = session.sectionName,
                currentIndex = currentIndex,
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
                    vertical = 14.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --------------------------------------------------
            // Progress Header
            // --------------------------------------------------

            ProgressSection(
                currentIndex = currentIndex,
                totalStudents = totalStudents,
                progress = progress,
                progressPercent = progressPercent
            )

            Spacer(modifier = Modifier.height(14.dp))

            // --------------------------------------------------
            // Attendance Statistics
            // --------------------------------------------------

            AttendanceStats(
                presentCount = presentCount,
                notPresentCount = notPresentCount
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --------------------------------------------------
            // Student Card
            // --------------------------------------------------

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

            // --------------------------------------------------
            // Attendance Actions
            // --------------------------------------------------

            AttendanceActions(
                enabled = currentStudent != null,
                onPresent = {
                    currentStudent?.let {
                        onRecordAttendance(it.studentId, true)
                    }
                },
                onNotPresent = {
                    currentStudent?.let {
                        onRecordAttendance(it.studentId, false)
                    }
                },
                onUndo = onUndo,
                undoEnabled = currentIndex > 0
            )
        }
    }
}

// ============================================================
// Top Bar
// ============================================================

@Composable
private fun AttendanceTopBar(
    sectionName: String,
    currentIndex: Int,
    onBack: () -> Unit,
    onUndo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyPrimary)
            .padding(
                start = 8.dp,
                end = 8.dp,
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
                text = sectionName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Attendance Session",
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.68f)
            )
        }

        if (currentIndex > 0) {
            IconButton(
                onClick = onUndo,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = "Undo",
                    tint = PureWhite
                )
            }
        }
    }
}

// ============================================================
// Progress Section
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
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = if (totalStudents > 0) {
                        "Student ${currentIndex + 1} of $totalStudents"
                    } else {
                        "No students"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = NeutralOnSurfaceSecondary
                )
            }

            Text(
                text = "$progressPercent%",
                style = MaterialTheme.typography.titleLarge,
                color = NavyPrimary
            )
        }

        Spacer(modifier = Modifier.height(9.dp))

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(10.dp)),
            color = NavyPrimary,
            trackColor = NeutralVariant
        )
    }
}

// ============================================================
// Attendance Statistics
// ============================================================

@Composable
private fun AttendanceStats(
    presentCount: Int,
    notPresentCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        AttendanceStatCard(
            label = "Present",
            value = presentCount.toString(),
            icon = Icons.Default.Check,
            backgroundColor = EmeraldContainer,
            contentColor = EmeraldOnContainer,
            modifier = Modifier.weight(1f)
        )

        AttendanceStatCard(
            label = "Not Present",
            value = notPresentCount.toString(),
            icon = Icons.Default.Close,
            backgroundColor = RedContainer,
            contentColor = RedOnContainer,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AttendanceStatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(
                horizontal = 13.dp,
                vertical = 11.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PureWhite.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(9.dp))

        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = contentColor
            )

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.85f)
            )
        }
    }
}

// ============================================================
// Current Student Card
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
            containerColor = PureWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 26.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Student icon

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(NavyContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = NavyOnContainer,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "CURRENT STUDENT",
                style = MaterialTheme.typography.labelSmall,
                color = NeutralOnSurfaceTertiary,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = student.studentName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyContainer)
                    .padding(
                        horizontal = 15.dp,
                        vertical = 8.dp
                    )
            ) {
                Text(
                    text = "ID  •  ${student.studentId}",
                    style = MaterialTheme.typography.labelLarge,
                    color = NavyOnContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (totalStudents > 0) {
                    "${currentIndex + 1} / $totalStudents"
                } else {
                    ""
                },
                style = MaterialTheme.typography.bodySmall,
                color = NeutralOnSurfaceTertiary
            )
        }
    }
}

// ============================================================
// Completed State
// ============================================================

@Composable
private fun AllStudentsCompletedCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmeraldContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
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
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PureWhite.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = EmeraldPresent,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Attendance Completed",
                style = MaterialTheme.typography.headlineSmall,
                color = EmeraldOnContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "All students have been recorded.",
                style = MaterialTheme.typography.bodyMedium,
                color = EmeraldOnContainer.copy(alpha = 0.8f)
            )
        }
    }
}

// ============================================================
// Attendance Actions
// ============================================================

@Composable
private fun AttendanceActions(
    enabled: Boolean,
    onPresent: () -> Unit,
    onNotPresent: () -> Unit,
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
                    contentColor = PureWhite,
                    disabledContainerColor = EmeraldContainer,
                    disabledContentColor = EmeraldOnContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(27.dp)
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = "Present",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Button(
                onClick = onNotPresent,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedNotPresent,
                    contentColor = PureWhite,
                    disabledContainerColor = RedContainer,
                    disabledContentColor = RedOnContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(27.dp)
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = "Absent",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Button(
            onClick = onUndo,
            enabled = undoEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(13.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeutralVariant,
                contentColor = NeutralOnSurfaceSecondary,
                disabledContainerColor = NeutralVariant.copy(alpha = 0.55f),
                disabledContentColor = NeutralOnSurfaceTertiary.copy(alpha = 0.55f)
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

            Spacer(modifier = Modifier.width(7.dp))

            Text(
                text = "Undo Previous",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
