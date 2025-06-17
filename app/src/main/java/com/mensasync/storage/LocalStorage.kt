package com.mensasync.storage

import com.mensasync.model.Tisch

interface LocalStorage {
    fun save(data: List<Tisch>)
    fun load(): List<Tisch>?
    fun exportAsJson(): String
    fun importFromJson(json: String): List<Tisch>
}