package com.mensasync.sync

class SyncServiceImpl(private val onDataReceived: (String) -> Unit) : SyncService {
    override fun startDiscovery() {
        // TODO: implement Bluetooth/WiFi Direct Discovery
    }

    override fun sendData(json: String) {
        // TODO: send JSON via Bluetooth/WiFi
    }

    override fun receiveData(json: String) {
        onDataReceived(json)
    }

    override fun mergeRemoteData(json: String) {
        // TODO: logic to merge data with local state
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}