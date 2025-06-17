package com.mensasync.viewmodel

import androidx.lifecycle.ViewModel
import com.mensasync.model.TableModel
import com.mensasync.storage.LocalStorage
import com.mensasync.sync.SyncService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.mensasync.model.Tisch

class MensaViewModel(
    private val tableModel: TableModel,
    private val storage: LocalStorage,
    private val sync: SyncService
) : ViewModel() {

    private val _tische = MutableStateFlow(storage.load() ?: tableModel.getCurrentState())
    val tische: StateFlow<List<Tisch>> = _tische

    fun belegeTisch(id: Int, name: String) {
        tableModel.selectTable(id, name)
        _tische.value = tableModel.getCurrentState()
        storage.save(_tische.value)
    }

    fun gibTischFrei(id: Int) {
        tableModel.releaseTable(id)
        _tische.value = tableModel.getCurrentState()
        storage.save(_tische.value)
    }

    fun exportiereAlsJson(): String = storage.exportAsJson()

    fun importiereVonJson(json: String) {
        val daten = storage.importFromJson(json)
        daten.forEach { tisch ->
            if (tisch.besetztVon != null) {
                tableModel.selectTable(tisch.id, tisch.besetztVon!!)
            } else {
                tableModel.releaseTable(tisch.id)
            }
        }
        _tische.value = daten
        storage.save(daten)
    }

    fun startenSync() {
        sync.startDiscovery()
    }
}