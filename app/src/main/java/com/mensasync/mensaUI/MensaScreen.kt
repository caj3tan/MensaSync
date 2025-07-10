package com.mensasync.mensaUI

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mensasync.mensaControl.MensaViewModel
import com.mensasync.mensaData.Table

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensaScreen(navController: NavHostController, viewModel: MensaViewModel) {
    val tables by viewModel.tables.collectAsState()
    var selectedTable by remember { mutableStateOf<Table?>(null) }
    val username = viewModel.username.value
    var showRenameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(viewModel.username.value) }
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hallo, $username") },
                actions = {
                    IconButton(onClick = { menuExpanded = true },
                        modifier = Modifier.testTag("menuButton")
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menü öffnen")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sync starten") },
                            onClick = {
                                menuExpanded = false
                                viewModel.startSync()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Status senden") },
                            onClick = {
                                menuExpanded = false
                                viewModel.sendCurrentState()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Name ändern") },
                            onClick = {
                                menuExpanded = false
                                showRenameDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Trennen und Beenden") },
                            onClick = {
                                menuExpanded = false
                                viewModel.stopSync()
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            TextField(
                value = viewModel.searchQuery.value,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Person suchen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            MensaMap(
                tables = tables,
                viewModel = viewModel,
                onTableClick = { selectedTable = it }
            )

            selectedTable?.let { table ->
                AlertDialog(
                    onDismissRequest = { selectedTable = null },
                    title = { Text("Tisch ${table.id}") },
                    text = {
                        Text(
                            when {
                                table.occupiedBy.isEmpty() -> "Dieser Tisch ist frei."
                                table.occupiedBy.contains(username) -> "Du sitzt an diesem Tisch."
                                else -> table.occupiedBy.joinToString(", ")
                            }
                        )
                    },
                    confirmButton = {
                        when {
                            table.occupiedBy.contains(username) -> {
                                TextButton(onClick = {
                                    viewModel.releaseTable(table.id, username)
                                    selectedTable = null
                                }) {
                                    Text("Tisch freigeben")
                                }
                            }
                            else -> {
                                TextButton(onClick = {
                                    viewModel.selectTable(table.id, username)
                                    selectedTable = null
                                }) {
                                    Text("Hier hinsetzen")
                                }
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedTable = null }) {
                            Text("Schließen")
                        }
                    }
                )
            }
        }
    }
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Name ändern") },
            text = {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Neuer Name") },
                    modifier = Modifier.testTag("renameInput")

                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateUserName(newName.trim())
                    showRenameDialog = false
                },
                    modifier = Modifier.testTag("renameConfirm")
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false },
                    modifier = Modifier.testTag("renameCancel")
                ) {
                    Text("Abbrechen")
                }
            }
        )
    }
}
