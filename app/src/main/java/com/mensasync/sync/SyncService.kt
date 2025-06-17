package com.mensasync.sync

interface SyncService {
    fun startDiscovery()
    fun sendData(json: String)
    fun receiveData(json: String)
    fun mergeRemoteData(json: String)
}