package com.mensasync.model

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
}
