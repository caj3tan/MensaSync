package com.mensasync.mensaData

interface TableModel {
    fun selectTable(id: Int, name: String)
    fun releaseTable(id: Int, name: String)
    fun getCurrentState(): List<Table>
    fun setState(tables: List<Table>)
}