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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.georgehany.quickattend.R
import com.georgehany.quickattend.data.local.entity.AttendanceRecord
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.data.local.entity.Student
import com.georgehany.quickattend.ui.theme.EmeraldContainer
import com.georgehany.quickattend.ui.theme.EmeraldOnContainer
import com.georgehany.quickattend.ui.theme.EmeraldPresent
import com.georgehany.quickattend.ui.theme.NavyPrimary
import com.georgehany.quickattend.ui.theme.RedContainer
import com.georgehany.quickattend.ui.theme.RedNotPresent
import com.georgehany.quickattend.ui.theme.RedOnContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    session: Session,
    students: List<Student>,
    records: Map<String, AttendanceRecord>,
    onExportExcel: () -> Unit,
    onBack: () -> Unit
) {
    val presentCount = records.values.count { it.isPresent }
    val notPresentCount = session.totalStudents - presentCount
    val rate = if (session.totalStudents > 0) (presentCount.toFloat() / session.totalStudents.toFloat()) * 100f else 0f

    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val startTimeStr = session.startTimeMs?.let { timeFormatter.format(Date(it)) } ?: "N/A"
    val endTimeStr = session.endTimeMs?.let { timeFormatter.format(Date(it)) } ?: "N/A"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Attendance Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Attendance Completed",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                        color = EmeraldPresent,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = session.sectionName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Date:", color = MaterialTheme.colorScheme.outline)
                        Text(text = session.date, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Start Time:", color = MaterialTheme.colorScheme.outline)
                        Text(text = startTimeStr, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "End Time:", color = MaterialTheme.colorScheme.outline)
                        Text(text = endTimeStr, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryMetricBox(
                            label = "Present",
                            value = presentCount.toString(),
                            bgColor = EmeraldContainer,
                            textColor = EmeraldOnContainer,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricBox(
                            label = "Not Present",
                            value = notPresentCount.toString(),
                            bgColor = RedContainer,
                            textColor = RedOnContainer,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricBox(
                            label = "Rate",
                            value = String.format(Locale.ENGLISH, "%.1f%%", rate),
                            bgColor = Color(0xFFDBEAFE),
                            textColor = NavyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onExportExcel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(text = "Export Excel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Student List Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(students, key = { it.studentId }) { student ->
                    val rec = records[student.studentId]
                    val isPresent = rec?.isPresent == true

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = student.studentName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "ID: ${student.studentId}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            if (isPresent) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Present",
                                        tint = EmeraldPresent
                                    )
                                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                                    Text("Present", color = EmeraldPresent, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = "Not Present",
                                        tint = RedNotPresent
                                    )
                                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                                    Text("Absent", color = RedNotPresent, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryMetricBox(
    label: String,
    value: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColor
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}
