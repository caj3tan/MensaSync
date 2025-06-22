package com.mensasync.model

import kotlinx.coroutines.flow.StateFlow

interface TableModel {
    fun selectTable(id: Int, name: String)
    fun releaseTable(id: Int, name: String)
    fun getCurrentState(): List<Table>
}