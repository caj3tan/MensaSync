package com.mensasync.mensaNetwork

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.*
import java.io.IOException
import java.io.OutputStream
import java.util.*

class BluetoothSyncService(
    private val context: Context,
    private val onJsonReceived: (String) -> Unit,
    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
) : SyncService {

    private val serviceUUID: UUID = UUID.fromString("12345678-1234-1234-1234-123456789abc")

    private var serverSocket: BluetoothServerSocket? = null
    private var socket: BluetoothSocket? = null
    private var connectionJob: Job? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun startDiscovery() {
        connectionJob = scope.launch {
            try {
                serverSocket = bluetoothAdapter
                    ?.listenUsingRfcommWithServiceRecord("MensaSync", serviceUUID)
                Log.d("BluetoothSync", "Waiting for connection...")

                val clientSocket = serverSocket?.accept()
                clientSocket?.let {
                    socket = it
                    handleIncomingConnection(it)
                }
            } catch (e: SecurityException) {
                Log.e("BluetoothSync", "Permission error in startDiscovery", e)
            } catch (e: IOException) {
                Log.e("BluetoothSync", "IOException in startDiscovery", e)
            }
        }
    }

    private fun handleIncomingConnection(bluetoothSocket: BluetoothSocket) {
        scope.launch {
            try {
                val inputStream = bluetoothSocket.inputStream
                val buffer = ByteArray(1024)
                val bytes = inputStream.read(buffer)
                val message = String(buffer, 0, bytes)
                Log.d("BluetoothSync", "Received JSON: $message")
                receiveData(message)
            } catch (e: IOException) {
                Log.e("BluetoothSync", "Error reading from input stream", e)
            }
        }
    }

    override fun sendData(json: String) {
        socket?.let { sock ->
            scope.launch {
                try {
                    val outputStream: OutputStream = sock.outputStream
                    outputStream.write(json.toByteArray())
                    outputStream.flush()
                    Log.d("BluetoothSync", "Sent JSON: $json")
                } catch (e: IOException) {
                    Log.e("BluetoothSync", "Error sending data", e)
                }
            }
        } ?: Log.e("BluetoothSync", "Socket is null, can't send data")
    }

    override fun receiveData(json: String) {
        onJsonReceived(json)
    }

    override fun mergeRemoteData(json: String) {
        receiveData(json)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun stop() {
        Log.d("BluetoothSync", "Stopping Bluetooth connections...")

        try {
            socket?.close()
            socket = null
        } catch (e: IOException) {
            Log.e("BluetoothSync", "Error closing client socket", e)
        }

        try {
            serverSocket?.close()
            serverSocket = null
        } catch (e: IOException) {
            Log.e("BluetoothSync", "Error closing server socket", e)
        }

        scope.cancel(CancellationException("Sync stopped"))
    }
}
