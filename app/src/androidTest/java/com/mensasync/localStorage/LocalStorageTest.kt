package com.mensasync.localStorage

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mensasync.mensaData.Table
import com.mensasync.mensaData.TableType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalStorageImplTest {

    private lateinit var context: Context
    private lateinit var storage: LocalStorage

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("mensa", Context.MODE_PRIVATE).edit().clear().commit()
        storage = LocalStorageImpl(context)
    }


    @Test
    fun `save and load returns same data`() {
        val input = listOf(
            Table(0, TableType.QUADRAT, 10, 20, mutableListOf("Alice"))
        )

        storage.save(input)
        val loaded = storage.load()

        assertNotNull(loaded)
        assertEquals(input.size, loaded!!.size)
        assertEquals(input[0].occupiedBy, loaded[0].occupiedBy)
    }

    @Test
    fun `load returns null or empty when nothing saved`() {
        val loaded = storage.load()
        assertTrue(loaded == null || loaded.isEmpty())
    }

    @Test
    fun `exportAsJson returns JSON containing expected data`() {
        val input = listOf(
            Table(1, TableType.LANG, 5, 5, mutableListOf("Bob"))
        )

        storage.save(input)
        val json = storage.exportAsJson()

        assertTrue(json.contains("Bob"))
        assertTrue(json.contains("LANG"))
        assertTrue(json.contains("id"))
    }

    @Test
    fun `importFromJson correctly reconstructs table data`() {
        val json = """
            [
                {"id":2,"type":"KURZ","x":0,"y":0,"occupiedBy":["Clara"]}
            ]
        """.trimIndent()

        val result = storage.importFromJson(json)

        assertEquals(1, result.size)
        assertEquals(2, result[0].id)
        assertEquals("Clara", result[0].occupiedBy.first())
    }

    @Test
    fun `combined serialization and deserialization works`() {
        val original = listOf(
            Table(3, TableType.QUADRAT, 3, 3, mutableListOf("Dora"))
        )

        storage.save(original)
        val json = storage.exportAsJson()
        val parsed = storage.importFromJson(json)

        assertEquals(original[0].id, parsed[0].id)
        assertEquals("Dora", parsed[0].occupiedBy.first())
    }
}
