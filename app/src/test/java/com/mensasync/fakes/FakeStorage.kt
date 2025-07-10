package com.mensasync.fakes
import com.mensasync.localStorage.LocalStorage
import com.mensasync.mensaData.Table

class FakeStorage : LocalStorage {
    var lastSaved: List<Table>? = null

    override fun save(data: List<Table>) {
        lastSaved = data.map { it.copy() }
    }

    override fun load(): List<Table>? = null

    override fun exportAsJson(): String =
        """[{"id":0,"type":"QUADRAT","occupiedBy":["testuser"],"x":0,"y":0}]"""

    override fun importFromJson(json: String): List<Table> =
        listOf(Table(0, com.mensasync.mensaData.TableType.QUADRAT, 0, 0, mutableListOf("testuser")))
}