package com.georgehany.quickattend.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.georgehany.quickattend.data.parser.ImportParseResult
import com.georgehany.quickattend.ui.theme.ErrorRed
import com.georgehany.quickattend.ui.theme.Success

// ================================================================
// CREATE SESSION DIALOG
// ================================================================

@Composable
fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onFileSelected: (
        uri: Uri,
        fileName: String?,
        sectionName: String
    ) -> Unit
) {
    var sectionName by remember {
        mutableStateOf("")
    }

    var selectedFileName by remember {
        mutableStateOf<String?>(null)
    }

    var selectedUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        if (uri != null) {
            selectedUri = uri

            selectedFileName =
                uri.lastPathSegment
                    ?.substringAfterLast("/")
                    ?.substringAfterLast(":")
                    ?: "Selected file"
        }
    }

    val canStart =
        sectionName.trim().isNotEmpty() &&
                selectedUri != null

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(13.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {

                    androidx.compose.foundation.layout.Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column {

                    Text(
                        text = "New Attendance Session",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Import your student list",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // ------------------------------------------------
                // SECTION
                // ------------------------------------------------

                Text(
                    text = "Section Name",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                OutlinedTextField(
                    value = sectionName,
                    onValueChange = {
                        sectionName = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp),
                    placeholder = {
                        Text("e.g. Section 1")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                // ------------------------------------------------
                // FILE
                // ------------------------------------------------

                Text(
                    text = "Student List",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                if (selectedFileName == null) {

                    OutlinedButton(
                        onClick = {
                            filePicker.launch(
                                arrayOf(
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                    "application/vnd.ms-excel",
                                    "text/csv",
                                    "text/plain",
                                    "*/*"
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(13.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            vertical = 14.dp
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "Choose Excel / CSV File",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                } else {

                    SelectedFileCard(
                        fileName = selectedFileName!!,
                        onChange = {
                            filePicker.launch(
                                arrayOf(
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                    "application/vnd.ms-excel",
                                    "text/csv",
                                    "text/plain",
                                    "*/*"
                                )
                            )
                        },
                        onRemove = {
                            selectedUri = null
                            selectedFileName = null
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // ------------------------------------------------
                // INFO
                // ------------------------------------------------

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.55f
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {

                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(9.dp)
                        )

                        Text(
                            text = "The file should contain student IDs and names. QuickAttend will automatically detect the relevant columns when possible.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {

            Button(
                onClick = {
                    val uri = selectedUri ?: return@Button

                    onFileSelected(
                        uri,
                        selectedFileName,
                        sectionName.trim()
                    )
                },
                enabled = canStart,
                shape = RoundedCornerShape(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 18.dp,
                    vertical = 10.dp
                )
            ) {

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "Start Session",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

// ================================================================
// SELECTED FILE
// ================================================================

@Composable
private fun SelectedFileCard(
    fileName: String,
    onChange: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
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

            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {

                androidx.compose.foundation.layout.Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.TableView,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = fileName,
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
                    text = "Student list selected",
                    style = MaterialTheme.typography.labelSmall,
                    color = Success
                )
            }

            TextButton(
                onClick = onChange
            ) {
                Text(
                    text = "Change",
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = onRemove
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove selected file",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ================================================================
// COLUMN MAPPING DIALOG
// ================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnMappingDialog(
    mappingData: ImportParseResult.ColumnMappingRequired,
    onConfirm: (idColumn: Int, nameColumn: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val columns = mappingData.columns

    var selectedIdColumn by remember {
        mutableStateOf(
            mappingData.detectedIdColIndex
                ?: columns.indices.firstOrNull()
                ?: 0
        )
    }

    var selectedNameColumn by remember {
        mutableStateOf(
            mappingData.detectedNameColIndex
                ?: columns.indices.getOrNull(1)
                ?: 0
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {

            Column {

                Text(
                    text = "Map Student Columns",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "QuickAttend could not confidently identify the required columns.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Column {

                    Text(
                        text = "Student ID Column",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    ColumnDropdown(
                        selectedIndex = selectedIdColumn,
                        columns = columns,
                        onSelected = {
                            selectedIdColumn = it
                        }
                    )
                }

                Column {

                    Text(
                        text = "Student Name Column",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    ColumnDropdown(
                        selectedIndex = selectedNameColumn,
                        columns = columns,
                        onSelected = {
                            selectedNameColumn = it
                        }
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.55f
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(11.dp),
                        verticalAlignment = Alignment.Top
                    ) {

                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "Select the column containing the student's unique ID and the column containing the student's full name.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {

            Button(
                onClick = {
                    if (selectedIdColumn != selectedNameColumn) {
                        onConfirm(
                            selectedIdColumn,
                            selectedNameColumn
                        )
                    }
                },
                enabled = selectedIdColumn != selectedNameColumn,
                shape = RoundedCornerShape(12.dp)
            ) {

                Text(
                    text = "Continue",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

// ================================================================
// COLUMN DROPDOWN
// ================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnDropdown(
    selectedIndex: Int,
    columns: List<String>,
    onSelected: (Int) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val selectedText =
        columns.getOrNull(selectedIndex)
            ?: "Select column"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(13.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.fillMaxWidth(0.82f)
        ) {

            columns.forEachIndexed { index, column ->

                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${index + 1}. $column",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}
