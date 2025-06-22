package com.mensasync.viewmodel

import androidx.compose.runtime.mutableStateOf
import com.mensasync.model.Table
import com.mensasync.model.TableModel
import com.mensasync.storage.LocalStorage
import com.mensasync.sync.SyncService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MensaViewModelImplTest {

    private lateinit var viewModel: MensaViewModelImpl
    private lateinit var fakeModel: FakeTableModel
    private lateinit var fakeStorage: FakeStorage
    private lateinit var fakeSync: FakeSync

    @Before
    fun setup() {
        fakeModel = FakeTableModel()
        fakeStorage = FakeStorage()
        fakeSync = FakeSync()
        viewModel = MensaViewModelImpl(fakeModel, fakeStorage, fakeSync)
    }

    @Test
    fun `updateUserName changes username`() {
        viewModel.updateUserName("Alice")
        assertEquals("Alice", viewModel.username.value)
    }

    @Test
    fun `selectTable updates model and persists`() = runTest {
        viewModel.updateUserName("Bob")
        viewModel.selectTable(0, "Bob")
        val table = viewModel.tables.first().first { it.id == 0 }
        assertTrue("Bob" in table.occupiedBy)
        assertEquals("Bob", fakeStorage.lastSaved?.first { it.id == 0 }?.occupiedBy?.first())
    }

    @Test
    fun `releaseTable frees up table`() = runTest {
        viewModel.selectTable(0, "Carl")
        viewModel.releaseTable(0, "Carl")
        val table = viewModel.tables.first().first { it.id == 0 }
        assertFalse("Carl" in table.occupiedBy)
    }

    @Test
    fun `updateSuche updates search name`() {
        viewModel.updateSearchQuery("Dana")
        assertEquals("Dana", viewModel.searchQuery.value)
    }

    @Test
    fun `importFromJson sets and persists data`() = runTest {
        val json = fakeStorage.exportAsJson() // already prefilled
        viewModel.importFromJson(json)
        assertEquals("testuser", viewModel.tables.first().first().occupiedBy?.first())
    }
}

class FakeTableModel : TableModel {
    private val tables = MutableList(5) { Table(id = 0, type = com.mensasync.model.TableType.QUADRAT, x = 10, y = 10) }

    override fun getCurrentState(): List<Table> = tables.map { it.copy() }

    override fun selectTable(id: Int, name: String) {
        val table = tables.first { it.id == id }
        if (table.occupiedBy == null) table.occupiedBy = mutableListOf()
        table.occupiedBy!!.add(name)
    }

    override fun releaseTable(id: Int, name: String) {
        tables.first { it.id == id }.occupiedBy?.remove(name)
    }
}

class FakeStorage : LocalStorage {
    var lastSaved: List<Table>? = null

    override fun save(data: List<Table>) {
        lastSaved = data.map { it.copy() }
    }

    override fun load(): List<Table>? = null

    override fun exportAsJson(): String =
        """[{"id":0,"type":"QUADRAT","occupiedBy":["testuser"],"x":0,"y":0}]"""

    override fun importFromJson(json: String): List<Table> =
        listOf(Table(0, com.mensasync.model.TableType.QUADRAT, 0, 0, mutableListOf("testuser")))
}

class FakeSync : SyncService {
    override fun startDiscovery() {}
    override fun stop() {}
    override fun sendData(json: String) {}
    override fun receiveData(json: String) {}
    override fun mergeRemoteData(json: String) {}
}
