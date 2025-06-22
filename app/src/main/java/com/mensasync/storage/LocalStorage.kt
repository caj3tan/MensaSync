package com.mensasync.storage

import com.mensasync.model.Table

interface LocalStorage {
    fun save(data: List<Table>)
    fun load(): List<Table>?
    fun exportAsJson(): String
    fun importFromJson(json: String): List<Table>
}