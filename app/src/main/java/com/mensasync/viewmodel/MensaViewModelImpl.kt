package com.mensasync.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.mensasync.model.Table
import com.mensasync.model.TableModel
import com.mensasync.storage.LocalStorage
import com.mensasync.sync.SyncService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MensaViewModelImpl(
    private val tableModel: TableModel,
    private val storage: LocalStorage,
    private val syncService: SyncService
) : ViewModel(), MensaViewModel {

    private val _tables = MutableStateFlow(tableModel.getCurrentState())
    override val tables: StateFlow<List<Table>> = _tables

    private val _username = mutableStateOf("")
    override val username: State<String> = _username

    private val _searchQuery = mutableStateOf("")
    override val searchQuery: State<String> = _searchQuery

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

    private fun updateAndPersist() {
        val updated = tableModel.getCurrentState()
        _tables.value = updated
        storage.save(updated)
    }

    override fun sendCurrentState() {
        val json = exportAsJson()
        syncService.sendData(json)
    }
}
