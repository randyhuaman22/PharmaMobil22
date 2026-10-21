package pe.edu.upeu.pharmamobil.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import pe.edu.upeu.pharmamobil.db.PharmaDb
import pe.edu.upeu.pharmamobil.domain.model.Producto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Base SQLite REAL en memoria para Android/JVM host; imposible usar JDBC en commonTest iOS. */
class SqlDelightDriverTest {
    @Test fun guardaPendienteYNoSePierdeAlReemplazarSincronizados() = runBlocking {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            PharmaDb.Schema.create(driver)
            val local = ProductoLocal(PharmaDb(driver))
            val guardado = local.guardarPendiente(Producto(0, "Pendiente", 4.5, 7))
            local.reemplazarSincronizados(listOf(Producto(15, "Servidor", 3.0, 10)))
            assertTrue(guardado.id < 0)
            assertEquals(1, local.pendientes().size)
            assertEquals(2, local.observar().first().size)
        } finally { driver.close() }
    }

    @Test fun conservaLaMarcaDeTiempo() = runBlocking {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            PharmaDb.Schema.create(driver)
            val local = ProductoLocal(PharmaDb(driver))
            assertEquals(null, local.observarUltimaSincronizacion().first())
            local.guardarUltimaSincronizacion(1000L)
            assertEquals(1000L, local.observarUltimaSincronizacion().first())
        } finally { driver.close() }
    }
}
