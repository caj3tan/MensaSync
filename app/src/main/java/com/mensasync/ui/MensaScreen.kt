package com.mensasync.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mensasync.viewmodel.MensaViewModel
import com.mensasync.model.Table

@Composable
fun MensaScreen(navController: NavHostController, viewModel: MensaViewModel) {
    val tische by viewModel.tables.collectAsState()
    var ausgewählterTisch by remember { mutableStateOf<Table?>(null) }

    val username = viewModel.username.value

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Hallo, $username", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.searchQuery.value,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Person suchen") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        MensaMap(tables = tische,
            viewModel = viewModel,
            onTableClick = { tisch ->
            ausgewählterTisch = tisch
        })

        ausgewählterTisch?.let { tisch ->
            AlertDialog(
                onDismissRequest = { ausgewählterTisch = null },
                title = {
                    Text("Tisch ${tisch.id}")
                },
                text = {
                    Text(
                        when {
                            tisch.occupiedBy.isEmpty() -> "Dieser Tisch ist frei."
                            tisch.occupiedBy.contains(username) -> "Du sitzt an diesem Tisch."
                            else -> tisch.occupiedBy.joinToString(", ")
                        }
                    )
                },
                confirmButton = {
                    when {
                        tisch.occupiedBy.contains(username) -> {
                            TextButton(onClick = {
                                viewModel.releaseTable(tisch.id, username)
                                ausgewählterTisch = null
                            }) {
                                Text("Tisch freigeben")
                            }
                        }
                        else -> {
                            TextButton(onClick = {
                                viewModel.selectTable(tisch.id, username)
                                ausgewählterTisch = null
                            }) {
                                Text("Hier hinsetzen")
                            }
                        }
                    }
                },

                dismissButton = {
                    TextButton(onClick = { ausgewählterTisch = null }) {
                        Text("Schließen")
                    }
                }
            )
        }
    }
}