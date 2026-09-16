package com.georgehany.quickattend.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.georgehany.quickattend.data.parser.ColumnMappingData
import com.georgehany.quickattend.ui.theme.ErrorRed
import com.georgehany.quickattend.ui.theme.SuccessGreen
import com.georgehany.quickattend.ui.theme.WarningAmber

@Composable
fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onFileSelected: (Uri, String, String) -> Unit
) {
    var sectionName by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedFileUri = uri
            selectedFileName = uri.lastPathSegment
                ?.substringAfterLast("/")
                ?: "Selected file"

            errorMessage = null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column {
                DialogIcon(
                    icon = Icons.Default.PlaylistAdd,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    iconColor = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Create New Session",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Set up your attendance session in a few simple steps.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = sectionName,
                    onValueChange = {
                        sectionName = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Section Name")
                    },
                    placeholder = {
                        Text("e.g. Section A")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(14.dp)
                )

                Text(
                    text = "Student List",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (selectedFileUri == null) {
                    OutlinedButton(
                        onClick = {
                            filePickerLauncher.launch("*/*")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Choose Student File",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    SelectedFileCard(
                        fileName = selectedFileName,
                        onRemove = {
                            selectedFileUri = null
                            selectedFileName = ""
                        }
                    )
                }

                if (selectedFileUri == null) {
                    Text(
                        text = "Supported formats depend on the imported student list.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                errorMessage?.let {
                    ErrorMessageCard(message = it)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        sectionName.isBlank() -> {
                            errorMessage = "Please enter a section name."
                        }

                        selectedFileUri == null -> {
                            errorMessage = "Please select a student file."
                        }

                        else -> {
                            onFileSelected(
                                selectedFileUri!!,
                                selectedFileName,
                                sectionName.trim()
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 20.dp,
                    vertical = 11.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Create Session",
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

@Composable
fun ColumnMappingDialog(
    mappingData: ColumnMappingData,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedIdColumn by remember {
        mutableStateOf(mappingData.detectedIdColumn)
    }

    var selectedNameColumn by remember {
        mutableStateOf(mappingData.detectedNameColumn)
    }

    var idExpanded by remember { mutableStateOf(false) }
    var nameExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column {
                DialogIcon(
                    icon = Icons.Default.TableChart,
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    iconColor = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Map Student Columns",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "We couldn't confidently identify the student ID and name columns.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MappingInfoCard()

                MappingDropdown(
                    label = "Student ID Column",
                    selectedIndex = selectedIdColumn,
                    columns = mappingData.columns,
                    expanded = idExpanded,
                    onExpandedChange = {
                        idExpanded = it
                        if (it) nameExpanded = false
                    },
                    onColumnSelected = {
                        selectedIdColumn = it
                        idExpanded = false
                    },
                    icon = Icons.Default.Badge
                )

                MappingDropdown(
                    label = "Student Name Column",
                    selectedIndex = selectedNameColumn,
                    columns = mappingData.columns,
                    expanded = nameExpanded,
                    onExpandedChange = {
                        nameExpanded = it
                        if (it) idExpanded = false
                    },
                    onColumnSelected = {
                        selectedNameColumn = it
                        nameExpanded = false
                    },
                    icon = Icons.Default.Person
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (
                        selectedIdColumn >= 0 &&
                        selectedNameColumn >= 0 &&
                        selectedIdColumn != selectedNameColumn
                    ) {
                        onConfirm(
                            selectedIdColumn,
                            selectedNameColumn
                        )
                    }
                },
                enabled =
                    selectedIdColumn >= 0 &&
                    selectedNameColumn >= 0 &&
                    selectedIdColumn != selectedNameColumn,
                shape = RoundedCornerShape(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 20.dp,
                    vertical = 11.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Confirm Mapping",
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

@Composable
private fun DialogIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    iconColor: Color
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun SelectedFileCard(
    fileName: String,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Selected file",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onRemove
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove selected file",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun ErrorMessageCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = ErrorRed.copy(alpha = 0.10f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MappingInfoCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Select which columns contain the student's ID and full name. The two columns must be different.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MappingDropdown(
    label: String,
    selectedIndex: Int,
    columns: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onColumnSelected: (Int) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = if (
                selectedIndex >= 0 &&
                selectedIndex < columns.size
            ) {
                columns[selectedIndex]
            } else {
                ""
            },
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = {
                Text(label)
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            shape = RoundedCornerShape(14.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            }
        ) {
            columns.forEachIndexed { index, columnName ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = columnName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onColumnSelected(index)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (index == selectedIndex) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen
                            )
                        }
                    }
                )
            }
        }
    }
}
