package com.mensasync.mensaData

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TableModelTest {

    private lateinit var tableModel: TableModelImpl

    @Before
    fun setUp() {
        tableModel = TableModelImpl()
    }

    @Test
    fun `selectTable marks the table as occupied`() {
        tableModel.selectTable(0, "Alice")
        val table = tableModel.getCurrentState().first { it.id == 0 }
        assertTrue("Alice" in table.occupiedBy)
    }

    @Test
    fun `selectTable replaces previous table for same user`() {
        tableModel.selectTable(1, "Bob")
        tableModel.selectTable(2, "Bob")
        val tables = tableModel.getCurrentState()

        val previouslySelected = tables.first { it.id == 1 }
        val currentlySelected = tables.first { it.id == 2 }

        assertFalse("Bob" in previouslySelected.occupiedBy)
        assertTrue("Bob" in currentlySelected.occupiedBy)
    }

    @Test
    fun `selectTable allows multiple users per table`() {
        tableModel.selectTable(3, "Alice")
        tableModel.selectTable(3, "Bob")

        val table = tableModel.getCurrentState().first { it.id == 3 }
        assertTrue("Alice" in table.occupiedBy)
        assertTrue("Bob" in table.occupiedBy)
    }

    @Test
    fun `releaseTable removes user from table`() {
        tableModel.selectTable(4, "Charlie")
        tableModel.releaseTable(4, "Charlie")
        val table = tableModel.getCurrentState().first { it.id == 4 }
        assertFalse("Charlie" in table.occupiedBy)
    }

    @Test
    fun `releaseTable does nothing if user not present`() {
        tableModel.selectTable(5, "Dana")
        tableModel.releaseTable(5, "SomeoneElse")
        val table = tableModel.getCurrentState().first { it.id == 5 }
        assertTrue("Dana" in table.occupiedBy)
        assertFalse("SomeoneElse" in table.occupiedBy)
    }

    @Test
    fun `initial state has all tables unoccupied`() {
        val tables = tableModel.getCurrentState()
        assertTrue(tables.all { it.occupiedBy.isEmpty() })
    }

    @Test(expected = NoSuchElementException::class)
    fun `selectTable with invalid ID throws exception`() {
        tableModel.selectTable(999, "GhostUser")
    }

    @Test(expected = NoSuchElementException::class)
    fun `releaseTable with invalid ID throws exception`() {
        tableModel.releaseTable(999, "GhostUser")
    }

    @Test
    fun `getCurrentState returns a copy not affecting internal state`() {
        val state = tableModel.getCurrentState()
        state[0].occupiedBy.add("Hacker")

        val newState = tableModel.getCurrentState()
        assertFalse("Hacker" in newState[0].occupiedBy)
    }

    @Test
    fun `setState replaces current table state`() {
        tableModel.selectTable(0, "Alice")
        val originalTable = tableModel.getCurrentState().first { it.id == 0 }
        assertTrue("Alice" in originalTable.occupiedBy)

        val newState = listOf(
            Table(id = 0, type = TableType.QUADRAT, x = 0, y = 0, occupiedBy = mutableListOf("Bob")),
            Table(id = 1, type = TableType.LANG, x = 50, y = 50, occupiedBy = mutableListOf("Carol"))
        )
        tableModel.setState(newState)

        val updatedTables = tableModel.getCurrentState()
        val replacedTable = updatedTables.first { it.id == 0 }
        val newTable = updatedTables.first { it.id == 1 }

        assertEquals(listOf("Bob"), replacedTable.occupiedBy)
        assertEquals(listOf("Carol"), newTable.occupiedBy)
        assertEquals(2, updatedTables.size)
    }

}
