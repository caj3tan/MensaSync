package com.mensasync.mensaUI

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mensasync.mensaControl.MensaViewModel

@Composable
fun StartScreen(navController: NavHostController, viewModel: MensaViewModel) {
    var name by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Willkommen bei MensaSync", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Dein Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
                .testTag("startNameInput")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.updateUserName(name)
                viewModel.startSync()
                navController.navigate(Screen.Mensa.route)
            },
            enabled = name.isNotBlank(),
            modifier = Modifier
                .align(Alignment.End)
                .testTag("connectButton")
        ) {
            Text("Verbinden")
        }
    }
}