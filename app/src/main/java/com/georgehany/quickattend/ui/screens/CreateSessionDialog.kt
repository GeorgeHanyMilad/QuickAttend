package com.georgehany.quickattend.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.georgehany.quickattend.data.parser.ImportParseResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onFileSelected: (uri: Uri, fileName: String?, sectionName: String) -> Unit
) {
    var sectionName by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            selectedFileName = uri.lastPathSegment ?: "Selected_File"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create New Session",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = sectionName,
                    onValueChange = { sectionName = it },
                    label = { Text("Section Name") },
                    placeholder = { Text("e.g. Database – Sec03 – L02 – Mon") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Text(
                        text = "Student List File (.xlsx, .xls, .csv, .txt)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = "Upload File"
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(
                            text = selectedFileName ?: "Select Excel / Text File",
                            maxLines = 1
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (sectionName.isBlank()) {
                        errorMessage = "Please enter section name."
                        return@Button
                    }
                    if (selectedFileUri == null) {
                        errorMessage = "Please select a student list file."
                        return@Button
                    }
                    onFileSelected(selectedFileUri!!, selectedFileName, sectionName.trim())
                    onDismiss()
                }
            ) {
                Text("Create Session")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnMappingDialog(
    mappingData: ImportParseResult.ColumnMappingRequired,
    onConfirm: (idColIndex: Int, nameColIndex: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedIdCol by remember { mutableIntStateOf(mappingData.detectedIdColIndex ?: 0) }
    var selectedNameCol by remember { mutableIntStateOf(mappingData.detectedNameColIndex ?: (if (mappingData.columns.size > 1) 1 else 0)) }

    var idExpanded by remember { mutableStateOf(false) }
    var nameExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Map Columns",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Please select which columns contain Student ID and Student Name:",
                    style = MaterialTheme.typography.bodyMedium
                )

                // ID Dropdown
                ExposedDropdownMenuBox(
                    expanded = idExpanded,
                    onExpandedChange = { idExpanded = !idExpanded }
                ) {
                    OutlinedTextField(
                        value = mappingData.columns.getOrElse(selectedIdCol) { "Column ${selectedIdCol + 1}" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Student ID Column") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = idExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = idExpanded,
                        onDismissRequest = { idExpanded = false }
                    ) {
                        mappingData.columns.forEachIndexed { index, colName ->
                            DropdownMenuItem(
                                text = { Text(colName) },
                                onClick = {
                                    selectedIdCol = index
                                    idExpanded = false
                                }
                            )
                        }
                    }
                }

                // Name Dropdown
                ExposedDropdownMenuBox(
                    expanded = nameExpanded,
                    onExpandedChange = { nameExpanded = !nameExpanded }
                ) {
                    OutlinedTextField(
                        value = mappingData.columns.getOrElse(selectedNameCol) { "Column ${selectedNameCol + 1}" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Student Name Column") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nameExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = nameExpanded,
                        onDismissRequest = { nameExpanded = false }
                    ) {
                        mappingData.columns.forEachIndexed { index, colName ->
                            DropdownMenuItem(
                                text = { Text(colName) },
                                onClick = {
                                    selectedNameCol = index
                                    nameExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedIdCol, selectedNameCol) }
            ) {
                Text("Confirm Mapping")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
