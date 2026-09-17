package com.georgehany.quickattend.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.georgehany.quickattend.data.parser.ImportParseResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnMappingDialog(
    mappingData: ImportParseResult.ColumnMappingRequired,
    onConfirm: (idColumn: Int, nameColumn: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val detectedId = mappingData.detectedIdColIndex ?: 0
    val detectedName = mappingData.detectedNameColIndex
        ?: if (mappingData.columns.size > 1) 1 else 0

    var selectedIdColumn by remember(detectedId) {
        mutableStateOf(detectedId)
    }

    var selectedNameColumn by remember(detectedName) {
        mutableStateOf(detectedName)
    }

    var idExpanded by remember {
        mutableStateOf(false)
    }

    var nameExpanded by remember {
        mutableStateOf(false)
    }

    val canConfirm =
        selectedIdColumn != selectedNameColumn &&
            selectedIdColumn in mappingData.columns.indices &&
            selectedNameColumn in mappingData.columns.indices

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.TableView,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(10.dp)
                )
            }
        },
        title = {
            Text(
                text = "Match Student Columns",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Text(
                            text = "We couldn't automatically identify the Student ID and Student Name columns. Select them below to continue.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text = "Student ID Column",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = idExpanded,
                    onExpandedChange = {
                        idExpanded = !idExpanded
                    }
                ) {
                    OutlinedTextField(
                        value = columnLabel(
                            mappingData.columns,
                            selectedIdColumn
                        ),
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        label = {
                            Text("Select ID column")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = idExpanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = idExpanded,
                        onDismissRequest = {
                            idExpanded = false
                        }
                    ) {
                        mappingData.columns.forEachIndexed { index, column ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = column.ifBlank {
                                            "Column ${index + 1}"
                                        },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onClick = {
                                    selectedIdColumn = index
                                    idExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "Student Name Column",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = nameExpanded,
                    onExpandedChange = {
                        nameExpanded = !nameExpanded
                    }
                ) {
                    OutlinedTextField(
                        value = columnLabel(
                            mappingData.columns,
                            selectedNameColumn
                        ),
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        label = {
                            Text("Select Name column")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = nameExpanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = nameExpanded,
                        onDismissRequest = {
                            nameExpanded = false
                        }
                    ) {
                        mappingData.columns.forEachIndexed { index, column ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = column.ifBlank {
                                            "Column ${index + 1}"
                                        },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onClick = {
                                    selectedNameColumn = index
                                    nameExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text = "File Preview",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        mappingData.rawRows
                            .take(4)
                            .forEach { row ->

                                Text(
                                    text = row.joinToString("  •  "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        selectedIdColumn,
                        selectedNameColumn
                    )
                },
                enabled = canConfirm,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Continue")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text("Cancel")
            }
        }
    )
}

private fun columnLabel(
    columns: List<String>,
    index: Int
): String {
    return columns
        .getOrNull(index)
        ?.ifBlank { "Column ${index + 1}" }
        ?: "Column ${index + 1}"
}
