package com.mensasync

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mensasync.mensaUI.Screen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mensasync.mensaData.TableModelImpl
import com.mensasync.localStorage.LocalStorageImpl
import com.mensasync.mensaNetwork.BluetoothSyncService
import com.mensasync.mensaNetwork.SyncService
import com.mensasync.mensaControl.MensaViewModel
import com.mensasync.mensaControl.MensaViewModelImpl
import com.mensasync.mensaUI.MensaScreen
import com.mensasync.mensaUI.StartScreen

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

        val dummySync = object : SyncService {
            override fun startDiscovery() {}
            override fun sendData(json: String) {}
            override fun receiveData(json: String) {}
            override fun mergeRemoteData(json: String) {}
            override fun stop() {}
        }

        val tableModel = TableModelImpl()
        val storage = LocalStorageImpl(this)
        val viewModel: MensaViewModel = MensaViewModelImpl(tableModel, storage, dummySync)
        val bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager).adapter
        val realSyncService = BluetoothSyncService(this, { json ->
            viewModel.importFromJson(json)
        }, bluetoothAdapter)

        viewModel.setSyncService(realSyncService)
        viewModel.selectTable(1, "Max")
        viewModel.selectTable(2, "Anna")
        viewModel.selectTable(8, "Lisa")
        viewModel.selectTable(12, "Claire")
        viewModel.selectTable(20, "Bob")

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
