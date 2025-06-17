package com.mensasync.viewmodel

import kotlinx.coroutines.flow.StateFlow
import com.mensasync.model.Tisch

interface MensaViewModelInterface {
    val tische: StateFlow<List<Tisch>>
    fun belegeTisch(id: Int, name: String)
    fun gibTischFrei(id: Int)
    fun exportiereAlsJson(): String
    fun importiereVonJson(json: String)
}