package com.mensasync.mensaControl

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.mensasync.mensaData.Table
import com.mensasync.mensaData.TableModel
import com.mensasync.localStorage.LocalStorage
import com.mensasync.mensaNetwork.SyncService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MensaViewModelImpl(
    private val tableModel: TableModel,
    private val storage: LocalStorage,
    private var syncService: SyncService
) : ViewModel(), MensaViewModel {

    private val _tables = MutableStateFlow(tableModel.getCurrentState())
    override val tables: StateFlow<List<Table>> = _tables

    private val _username = mutableStateOf("")
    override val username: State<String> = _username

    private val _searchQuery = mutableStateOf("")
    override val searchQuery: State<String> = _searchQuery

    init {
        storage.load()?.let { savedTables ->
            tableModel.setState(savedTables)
            updateAndPersist()
        }
    }

    override fun updateUserName(name: String) {
        _username.value = name
    }

    override fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    override fun selectTable(id: Int, name: String) {
        tableModel.selectTable(id, name)
        updateAndPersist()
    }

    override fun releaseTable(id: Int, name: String) {
        tableModel.releaseTable(id, name)
        updateAndPersist()
    }

    override fun exportAsJson(): String = storage.exportAsJson()

    override fun importFromJson(json: String) {
        val data = storage.importFromJson(json)
        data.forEach { table ->
            table.occupiedBy.forEach { occupant ->
                tableModel.selectTable(table.id, occupant)
            }
        }
        updateAndPersist()
    }

    override fun startSync() {
        syncService.startDiscovery()
    }

    override fun setSyncService(syncService: SyncService) {
        this.syncService = syncService
    }

    private fun updateAndPersist() {
        val updated = tableModel.getCurrentState()
        _tables.value = updated
        storage.save(updated)
    }

    override fun sendCurrentState() {
        val json = exportAsJson()
        syncService.sendData(json)
    }

    override fun stopSync() {
        syncService.stop()
    }

}
