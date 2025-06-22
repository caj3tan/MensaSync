package com.mensasync.sync

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.concurrent.thread

class BluetoothHelper(
    private val context: Context,
    private val onDataReceived: (String) -> Unit
) : SyncService {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val serviceUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var serverSocket: BluetoothServerSocket? = null

    private val connectedSockets = mutableListOf<BluetoothSocket>()

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun startDiscovery() {
        if (bluetoothAdapter == null) {
            Log.e("BluetoothHelper", "Bluetooth not supported")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
        ) {
            Log.w("BluetoothHelper", "Missing BLUETOOTH_CONNECT permission")
            return
        }

        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MensaSync", serviceUUID)

            thread(start = true) {
                while (true) {
                    try {
                        val socket = serverSocket?.accept()
                        socket?.let {
                            connectedSockets.add(it)
                            handleIncomingConnection(it)
                        }
                    } catch (e: IOException) {
                        Log.e("BluetoothHelper", "Error accepting connection", e)
                        break
                    }
                }
            }

        } catch (e: SecurityException) {
            Log.e("BluetoothHelper", "Permission denied", e)
        } catch (e: IOException) {
            Log.e("BluetoothHelper", "Could not open server socket", e)
        }
    }

    override fun sendData(json: String) {
        connectedSockets.forEach { socket ->
            try {
                val out: OutputStream = socket.outputStream
                out.write(json.toByteArray(Charsets.UTF_8))
                out.flush()
            } catch (e: IOException) {
                Log.e("BluetoothHelper", "Error sending data", e)
            }
        }
    }

    private fun handleIncomingConnection(socket: BluetoothSocket) {
        thread(start = true) {
            try {
                val input: InputStream = socket.inputStream
                val buffer = ByteArray(1024)
                val bytes = input.read(buffer)
                val received = String(buffer, 0, bytes, Charsets.UTF_8)
                onDataReceived(received)
            } catch (e: IOException) {
                Log.e("BluetoothHelper", "Error reading data", e)
            }
        }
    }

    override fun receiveData(json: String) {
        // Optional: Not needed – handled automatically in handleIncomingConnection
    }

    override fun mergeRemoteData(json: String) {
        onDataReceived(json) // Delegated to ViewModel or higher layer
    }

    override fun stop() {
        try {
            serverSocket?.close()
            connectedSockets.forEach { it.close() }
            connectedSockets.clear()
        } catch (e: IOException) {
            Log.e("BluetoothHelper", "Error closing sockets", e)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
