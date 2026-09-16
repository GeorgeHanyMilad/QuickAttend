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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.georgehany.quickattend.data.parser.ImportParseResult
import com.georgehany.quickattend.ui.theme.AccentBlueLight
import com.georgehany.quickattend.ui.theme.NavyContainer
import com.georgehany.quickattend.ui.theme.NavyOnContainer
import com.georgehany.quickattend.ui.theme.NavyPrimary
import com.georgehany.quickattend.ui.theme.NeutralOnSurfaceSecondary
import com.georgehany.quickattend.ui.theme.NeutralOnSurfaceTertiary
import com.georgehany.quickattend.ui.theme.RedContainer
import com.georgehany.quickattend.ui.theme.RedOnContainer
import com.georgehany.quickattend.ui.theme.PureWhite

// ============================================================
// Create Session Dialog
// ============================================================

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
            errorMessage = null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = PureWhite,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                DialogIcon(
                    icon = Icons.Default.UploadFile,
                    backgroundColor = NavyContainer,
                    iconColor = NavyOnContainer
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Create New Session",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Add a section and import its student list.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralOnSurfaceSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // --------------------------------------------------
                // Section Name
                // --------------------------------------------------

                OutlinedTextField(
                    value = sectionName,
                    onValueChange = {
                        sectionName = it
                        errorMessage = null
                    },
                    label = {
                        Text("Section Name")
                    },
                    placeholder = {
                        Text(
                            "e.g. Database – Sec03 – L02 – Mon"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                // --------------------------------------------------
                // File Picker
                // --------------------------------------------------

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Student List",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Excel, CSV or text files are supported.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralOnSurfaceSecondary
                    )

                    Spacer(modifier = Modifier.height(9.dp))

                    OutlinedButton(
                        onClick = {
                            filePickerLauncher.launch("*/*")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NavyPrimary
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = selectedFileName ?: "Select Student List",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // --------------------------------------------------
                // Selected File
                // --------------------------------------------------

                selectedFileName?.let { fileName ->

                    SelectedFileCard(
                        fileName = fileName
                    )
                }

                // --------------------------------------------------
                // Error
                // --------------------------------------------------

                errorMessage?.let { message ->

                    ErrorMessageCard(
                        message = message
                    )
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
                            errorMessage = "Please select a student list file."
                        }

                        else -> {
                            onFileSelected(
                                selectedFileUri!!,
                                selectedFileName,
                                sectionName.trim()
                            )

                            onDismiss()
                        }
                    }
                },
                enabled = sectionName.isNotBlank() && selectedFileUri != null,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyPrimary,
                    contentColor = PureWhite
                )
            ) {
                Text(
                    text = "Create Session",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeutralOnSurfaceSecondary
                )
            }
        }
    )
}

// ============================================================
// Column Mapping Dialog
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnMappingDialog(
    mappingData: ImportParseResult.ColumnMappingRequired,
    onConfirm: (idColIndex: Int, nameColIndex: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedIdCol by remember {
        mutableIntStateOf(
            mappingData.detectedIdColIndex ?: 0
        )
    }

    var selectedNameCol by remember {
        mutableIntStateOf(
            mappingData.detectedNameColIndex
                ?: if (mappingData.columns.size > 1) 1 else 0
        )
    }

    var idExpanded by remember { mutableStateOf(false) }
    var nameExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = PureWhite,

        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                DialogIcon(
                    icon = Icons.Default.Description,
                    backgroundColor = AccentBlueLight,
                    iconColor = NavyPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Map Student Columns",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tell QuickAttend which columns contain the student ID and name.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralOnSurfaceSecondary
                )
            }
        },

        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // --------------------------------------------------
                // Information Card
                // --------------------------------------------------

                MappingInfoCard()

                // --------------------------------------------------
                // Student ID Column
                // --------------------------------------------------

                MappingDropdown(
                    label = "Student ID Column",
                    selectedValue = mappingData.columns.getOrElse(
                        selectedIdCol
                    ) {
                        "Column ${selectedIdCol + 1}"
                    },
                    expanded = idExpanded,
                    onExpandedChange = {
                        idExpanded = it

                        if (it) {
                            nameExpanded = false
                        }
                    },
                    columns = mappingData.columns,
                    selectedIndex = selectedIdCol,
                    onSelect = { index ->
                        selectedIdCol = index
                        idExpanded = false
                    }
                )

                // --------------------------------------------------
                // Student Name Column
                // --------------------------------------------------

                MappingDropdown(
                    label = "Student Name Column",
                    selectedValue = mappingData.columns.getOrElse(
                        selectedNameCol
                    ) {
                        "Column ${selectedNameCol + 1}"
                    },
                    expanded = nameExpanded,
                    onExpandedChange = {
                        nameExpanded = it

                        if (it) {
                            idExpanded = false
                        }
                    },
                    columns = mappingData.columns,
                    selectedIndex = selectedNameCol,
                    onSelect = { index ->
                        selectedNameCol = index
                        nameExpanded = false
                    }
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {
                    onConfirm(
                        selectedIdCol,
                        selectedNameCol
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyPrimary,
                    contentColor = PureWhite
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Confirm Mapping",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeutralOnSurfaceSecondary
                )
            }
        }
    )
}

// ============================================================
// Dialog Icon
// ============================================================

@Composable
private fun DialogIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    iconColor: Color
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(15.dp))
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

// ============================================================
// Selected File Card
// ============================================================

@Composable
private fun SelectedFileCard(
    fileName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(NavyContainer)
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(PureWhite.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = NavyOnContainer,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(9.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Selected file",
                style = MaterialTheme.typography.labelSmall,
                color = NeutralOnSurfaceTertiary
            )

            Text(
                text = fileName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = NavyOnContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================
// Error Message
// ============================================================

@Composable
private fun ErrorMessageCard(
    message: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RedContainer)
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = RedOnContainer,
            modifier = Modifier.size(19.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = RedOnContainer,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ============================================================
// Mapping Information Card
// ============================================================

@Composable
private fun MappingInfoCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(AccentBlueLight.copy(alpha = 0.65f))
            .padding(
                horizontal = 12.dp,
                vertical = 11.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = NavyPrimary,
            modifier = Modifier.size(19.dp)
        )

        Spacer(modifier = Modifier.width(9.dp))

        Text(
            text = "Choose the correct columns from your imported file.",
            style = MaterialTheme.typography.bodySmall,
            color = NavyOnContainer
        )
    }
}

// ============================================================
// Mapping Dropdown
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MappingDropdown(
    label: String,
    selectedValue: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    columns: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(label)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
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
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onSelect(index)
                    },
                    leadingIcon = {
                        if (index == selectedIndex) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = NavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}
