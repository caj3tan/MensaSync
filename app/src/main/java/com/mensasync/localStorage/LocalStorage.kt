package com.mensasync.localStorage

import com.mensasync.mensaData.Table

interface LocalStorage {
    fun save(data: List<Table>)
    fun load(): List<Table>?
    fun exportAsJson(): String
    fun importFromJson(json: String): List<Table>
}