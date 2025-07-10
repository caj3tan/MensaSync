package com.mensasync.mensaControl

import com.mensasync.fakes.FakeSyncService
import com.mensasync.fakes.FakeTableModel
import com.mensasync.fakes.FakeStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MensaViewModelTest {

    private lateinit var viewModel: MensaViewModelImpl
    private lateinit var fakeModel: FakeTableModel
    private lateinit var fakeStorage: FakeStorage
    private lateinit var fakeSync: FakeSyncService

    @Before
    fun setup() {
        fakeModel = FakeTableModel()
        fakeStorage = FakeStorage()
        fakeSync = FakeSyncService()
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

    @Test
    fun `startSync calls startDiscovery on SyncService`() {
        val sync = FakeSyncService()
        val vm = MensaViewModelImpl(fakeModel, fakeStorage, sync)
        vm.startSync()
        assertTrue(sync.started)
    }

    @Test
    fun `stopSync calls stop on SyncService`() {
        val sync = FakeSyncService()
        val vm = MensaViewModelImpl(fakeModel, fakeStorage, sync)
        vm.stopSync()
        assertTrue(sync.stopped)
    }

    @Test
    fun `sendCurrentState calls sendData on SyncService`() = runTest {
        val fakeModel = FakeTableModel()
        val fakeStorage = FakeStorage()

        val viewModel = MensaViewModelImpl(fakeModel, fakeStorage, fakeSync)
        viewModel.sendCurrentState()

        assertTrue(fakeSync.called)
    }

    @Test
    fun `setSyncService replaces current SyncService`() {
        val newSync = FakeSyncService()
        viewModel.setSyncService(newSync)
        viewModel.startSync()
        assertTrue(newSync.started)
    }

}