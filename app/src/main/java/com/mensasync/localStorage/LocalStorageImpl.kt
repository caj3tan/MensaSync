package com.mensasync.localStorage

import android.content.Context
import com.mensasync.mensaData.Table
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.core.content.edit

class LocalStorageImpl(private val context: Context) : LocalStorage {
    private val prefs = context.getSharedPreferences("mensa", Context.MODE_PRIVATE)

    override fun save(data: List<Table>) {
        prefs.edit { putString("data", Json.encodeToString(data)) }
    }

    override fun load(): List<Table>? {
        return prefs.getString("data", null)?.let {
            Json.decodeFromString(it)
        }
    }

    override fun exportAsJson(): String {
        return prefs.getString("data", "[]") ?: "[]"
    }

    override fun importFromJson(json: String): List<Table> {
        save(Json.decodeFromString(json))
        return Json.decodeFromString(json)
    }
}