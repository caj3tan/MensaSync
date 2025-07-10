package com.mensasync.mensaData

class TableModelImpl : TableModel {

    private val tables = generateMensaTables().toMutableList()

    override fun selectTable(id: Int, name: String) {
        val table = tables.firstOrNull { it.id == id }
            ?: throw NoSuchElementException("Kein Tisch mit ID $id gefunden.")
        tables.forEach { it.occupiedBy.remove(name) }
        tables.firstOrNull { it.id == id }?.occupiedBy?.add(name)
    }

    override fun releaseTable(id: Int, name: String) {
        val table = tables.firstOrNull { it.id == id }
            ?: throw NoSuchElementException("Kein Tisch mit ID $id gefunden.")
        tables.firstOrNull { it.id == id }?.occupiedBy?.remove(name)
    }

    override fun getCurrentState(): List<Table> = tables.map {
        it.copy(occupiedBy = it.occupiedBy.toMutableList())
    }

    override fun setState(tables: List<Table>) {
        this.tables.clear()
        this.tables.addAll(tables.map { it.copy() })
    }
}



private fun generateMensaTables(): List<Table> {
    val spacing = 100
    val rawTables = mutableListOf<Table>()
    var id = 0
    var currentY = 0

    // Obere 6 quadratischen (Fensterseite)
    repeat(6) { i ->
        rawTables.add(Table(id++, TableType.QUADRAT, x = i * spacing, y = currentY))
    }
    currentY += spacing

    // Wiederholende Blöcke
    repeat(3) {
        // 6 lange quer
        repeat(6) { i ->
            rawTables.add(Table(id++, TableType.LANG, x = i * spacing, y = currentY))
        }
        currentY += spacing * 5 / 2

        // 2 Reihen kurze längs
        repeat(2) { row ->
            repeat(6) { i ->
                rawTables.add(Table(id++, TableType.KURZ, x = i * spacing, y = currentY + row * spacing * 2 / 3))
            }
        }
        currentY += spacing * 3 / 2
    }
    repeat(6) { i ->
        rawTables.add(Table(id++, TableType.LANG, x = i * spacing, y = currentY))
    }

    currentY += spacing * 5 / 2

    // Untere 6 quadratischen (Eingangsseite)
    repeat(6) { i ->
        rawTables.add(Table(id++, TableType.QUADRAT, x = i * spacing, y = currentY))
    }

    // 1. Drehung (90° im Uhrzeigersinn)
    val rotated = rawTables.map {
        it.copy(x = it.y, y = -it.x)
    }

    // 2. Normalisieren auf positive Koordinaten
    val minX = rotated.minOf { it.x }
    val minY = rotated.minOf { it.y }

    return rotated.map {
        it.copy(x = it.x - minX, y = it.y - minY)
    }
}
