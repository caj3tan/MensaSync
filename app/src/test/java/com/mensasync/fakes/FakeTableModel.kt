package com.mensasync.fakes
import com.mensasync.mensaData.Table
import com.mensasync.mensaData.TableModel
import com.mensasync.mensaData.TableType

class FakeTableModel : TableModel {
    private val tables = List(5) { Table(id = it, type = TableType.QUADRAT, x = 0, y = 0) }

    override fun getCurrentState(): List<Table> = tables.map { it.copy() }
    override fun setState(tables: List<Table>) {
        TODO("Not yet implemented")
    }

    override fun selectTable(id: Int, name: String) {
        val table = tables.first { it.id == id }
        if (table.occupiedBy == null) table.occupiedBy = mutableListOf()
        table.occupiedBy!!.add(name)
    }

    override fun releaseTable(id: Int, name: String) {
        tables.first { it.id == id }.occupiedBy?.remove(name)
    }
}
