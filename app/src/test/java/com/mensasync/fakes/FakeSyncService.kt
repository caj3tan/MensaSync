package com.mensasync.fakes
import com.mensasync.mensaNetwork.SyncService

class FakeSyncService : SyncService {
    var started = false
    var stopped = false
    var called = false

    override fun startDiscovery() {
        println("Fake discovery started.")
        started = true
    }

    override fun sendData(json: String) {
        println("Fake send: $json")
        called = true
    }

    override fun receiveData(json: String) {
        println("Fake receive: $json")
    }

    override fun mergeRemoteData(json: String) {
        println("Fake merge: $json")
    }

    override fun stop() {
        println("Fake stop.")
        stopped = true
    }
}
