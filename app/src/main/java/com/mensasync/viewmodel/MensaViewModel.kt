package com.mensasync.viewmodel

import androidx.compose.runtime.State
import com.mensasync.model.Table
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
    fun sendCurrentState()
}
