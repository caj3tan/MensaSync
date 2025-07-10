package com.mensasync.mensaControl

import androidx.compose.runtime.State
import com.mensasync.mensaData.Table
import com.mensasync.mensaNetwork.SyncService
import kotlinx.coroutines.flow.StateFlow

interface MensaViewModel {
    val tables: StateFlow<List<Table>>
    val username: State<String>
    val searchQuery: State<String>

    fun updateUserName(name: String)
    fun updateSearchQuery(query: String)

    fun selectTable(id: Int, name: String)
    fun releaseTable(id: Int, name: String)

    fun exportAsJson(): String
    fun importFromJson(json: String)

    fun startSync()
    fun stopSync()
    fun sendCurrentState()
    fun setSyncService(syncService: SyncService)
}
