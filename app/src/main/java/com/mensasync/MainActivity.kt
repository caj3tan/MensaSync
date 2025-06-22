package com.mensasync

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mensasync.ui.Screen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mensasync.model.TableModelImpl
import com.mensasync.storage.LocalStorageImpl
import com.mensasync.sync.BluetoothHelper
import com.mensasync.sync.SyncServiceImpl
import com.mensasync.viewmodel.MensaViewModel
import com.mensasync.viewmodel.MensaViewModelImpl
import com.mensasync.ui.MensaScreen
import com.mensasync.ui.StartScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    private val requiredPermissions = arrayOf(
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_ADVERTISE,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_ADVERTISE
            )
            val notGranted = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (notGranted.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, notGranted.toTypedArray(), 1)
            }
        } else {
            // Für ältere Android-Versionen (z. B. API 30): ACCESS_FINE_LOCATION erlaubt Bluetooth-Scanning
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestBluetoothPermissions()

        val tableModel = TableModelImpl()
        val storage = LocalStorageImpl(this)
        val sync = SyncServiceImpl { json ->
            println("Empfangenes JSON: $json")
        }
        val viewModel: MensaViewModel = MensaViewModelImpl(tableModel, storage, sync)
        val syncService = BluetoothHelper(this) { json ->
            viewModel.importFromJson(json)
        }



        viewModel.selectTable(0, "Anna")
        viewModel.selectTable(0, "Karl")
        viewModel.selectTable(5, "Max")

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = Screen.Start.route) {
                composable(Screen.Start.route) {
                    StartScreen(navController, viewModel)
                }
                composable(Screen.Mensa.route) {
                    MensaScreen(navController, viewModel)
                }
            }
        }
    }
}
