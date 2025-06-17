package com.mensasync.model

interface TableModel {
    fun selectTable(id: Int, name: String)
    fun releaseTable(id: Int)
    fun getCurrentState(): List<Tisch>
}