package com.mensasync.model

import kotlinx.serialization.Serializable

enum class TableType {
    QUADRAT, LANG, KURZ
}

@Serializable
data class Table(
    val id: Int,
    val type: TableType,
    val x: Int,
    val y: Int,
    var occupiedBy: MutableList<String> = mutableListOf()
)

data class TableRow(
    val tables: List<Table>,
    val istGang: Boolean = false
)