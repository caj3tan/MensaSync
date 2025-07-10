package com.mensasync.mensaNetwork

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.test.assertEquals


@RunWith(org.robolectric.RobolectricTestRunner::class)
class BluetoothSyncServiceTest {

    private lateinit var context: Context
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var socket: BluetoothSocket
    private lateinit var service: BluetoothSyncService

    private lateinit var receivedJson: String
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: ByteArrayOutputStream

    @Before
    fun setup() {
        context = mock()
        bluetoothAdapter = mock()
        socket = mock()
        inputStream = mock()
        outputStream = ByteArrayOutputStream()

        whenever(socket.outputStream).thenReturn(outputStream)
        whenever(socket.inputStream).thenReturn(inputStream)

        receivedJson = ""

        service = BluetoothSyncService(
            context = context,
            onJsonReceived = { json -> receivedJson = json },
            bluetoothAdapter = bluetoothAdapter
        )

        // Intern Socket setzen (normalerweise nur beim Verbindungsaufbau)
        val socketField = BluetoothSyncService::class.java.getDeclaredField("socket")
        socketField.isAccessible = true
        socketField.set(service, socket)
    }

    @Test
    fun `sendData writes JSON to outputStream`() {
        val testJson = """{"key":"value"}"""

        service.sendData(testJson)
        Thread.sleep(100) // Kurz warten, da Coroutine IO verwendet

        val sent = outputStream.toString()
        assertEquals(testJson, sent)
    }

    @Test
    fun `receiveData invokes callback`() {
        val testJson = """{"foo":"bar"}"""
        service.receiveData(testJson)
        assertEquals(testJson, receivedJson)
    }

    @Test
    fun `mergeRemoteData delegates to receiveData`() {
        val testJson = """{"merged":true}"""
        service.mergeRemoteData(testJson)
        assertEquals(testJson, receivedJson)
    }

    @Test
    fun `stop closes socket and serverSocket`() {
        val serverSocketMock = mock<android.bluetooth.BluetoothServerSocket>()

        val serverSocketField = BluetoothSyncService::class.java.getDeclaredField("serverSocket")
        serverSocketField.isAccessible = true
        serverSocketField.set(service, serverSocketMock)

        service.stop()

        verify(socket).close()
        verify(serverSocketMock).close()
    }
}
