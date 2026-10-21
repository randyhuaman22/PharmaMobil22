package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import pe.edu.upeu.pharmamobil.data.local.ProductoAlmacen
import pe.edu.upeu.pharmamobil.domain.model.Producto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Pruebas PORTABLES de commonTest. JDBC sólo existe en JVM: ver androidHostTest para SQLite real. */
class ProductoRepositorioOfflineTest {
    private fun p(id: Long, nombre: String = "Medicamento", pendiente: Boolean = false) =
        Producto(id, nombre, 4.5, 20, pendiente)

    private class BaseEnMemoria : ProductoAlmacen {
        private val filas = mutableMapOf<Long, Producto>()
        private val emisor = MutableStateFlow<List<Producto>>(emptyList())
        private val hora = MutableStateFlow<Long?>(null)
        private fun emitir() { emisor.value = filas.values.sortedBy { it.nombre } }
        override fun observar(): Flow<List<Producto>> = emisor
        override fun pendientes() = filas.values.filter { it.pendiente }.sortedBy { it.id }
        override fun obtener(id: Long) = filas[id]
        override fun guardarPendiente(producto: Producto): Producto {
            val nuevoId = minOf(filas.keys.minOrNull() ?: 0L, 0L) - 1
            val nuevo = producto.copy(id = nuevoId, pendiente = true)
            filas[nuevoId] = nuevo; emitir(); return nuevo
        }
        override fun confirmarPendiente(idLocal: Long, productoRemoto: Producto) {
            filas.remove(idLocal)
            filas[productoRemoto.id] = productoRemoto.copy(pendiente = false)
            emitir()
        }
        override fun reemplazarSincronizados(productos: List<Producto>) {
            filas.entries.removeAll { !it.value.pendiente }
            productos.forEach { filas[it.id] = it.copy(pendiente = false) }
            emitir()
        }
        override fun guardarActualizacion(producto: Producto) { filas[producto.id] = producto; emitir() }
        override fun eliminar(id: Long) { filas.remove(id); emitir() }
        override fun observarUltimaSincronizacion(): Flow<Long?> = hora
        override fun guardarUltimaSincronizacion(instante: Long) { hora.value = instante }
    }

    private class Servidor : ProductoApi {
        val productos = mutableMapOf<Long, Producto>()
        var disponible = true
        var siguienteId = 1L
        var posts = 0
        private fun verificar() { if (!disponible) throw IllegalStateException("SinConexion") }
        override suspend fun listar(): List<Producto> { verificar(); return productos.values.toList() }
        override suspend fun registrar(producto: Producto): Producto {
            verificar(); posts++
            val remoto = producto.copy(id = siguienteId++, pendiente = false)
            productos[remoto.id] = remoto
            return remoto
        }
        override suspend fun actualizar(producto: Producto): Producto { verificar(); productos[producto.id] = producto; return producto }
        override suspend fun eliminar(id: Long) { verificar(); productos.remove(id) }
        override suspend fun obtener(id: Long): Producto { verificar(); return requireNotNull(productos[id]) }
    }

    @Test fun sincronizacionConRedActualizaLaBaseYElFlow() = runTest {
        val local = BaseEnMemoria(); val server = Servidor()
        server.productos[5] = p(5, "Paracetamol")
        val repo = ProductoRepositorioOffline(local, server, { 1234L })
        repo.sincronizar()
        assertEquals(5L, repo.observar().first().single().id)
        assertEquals(1234L, repo.observarUltimaSincronizacion().first())
    }

    @Test fun registroSinRedQuedaPendienteYVisible() = runTest {
        val local = BaseEnMemoria(); val server = Servidor().also { it.disponible = false }
        val repo = ProductoRepositorioOffline(local, server)
        repo.registrar(p(0, "Ibuprofeno"))
        assertEquals(1, local.pendientes().size)
        assertTrue(repo.observar().first().single().pendiente)
        assertTrue(repo.observar().first().single().id < 0)
    }

    @Test fun envioAlReconectarConfirmaIdYQuitaPendiente() = runTest {
        val local = BaseEnMemoria(); val server = Servidor().also { it.disponible = false }
        val repo = ProductoRepositorioOffline(local, server)
        repo.registrar(p(0))
        server.disponible = true
        repo.sincronizar()
        assertTrue(local.pendientes().isEmpty())
        assertTrue(repo.observar().first().single().id > 0)
        assertEquals(1, server.posts)
    }

    @Test fun bajadaDelServidorNoBorraPendientes() = runTest {
        val local = BaseEnMemoria(); val server = Servidor()
        local.guardarPendiente(p(0, "Pendiente"))
        local.reemplazarSincronizados(listOf(p(6, "Remoto")))
        assertEquals(2, local.observar().first().size)
        assertEquals(1, local.pendientes().size)
    }

    @Test fun idsNegativosYPositivosNoColisionan() = runTest {
        val local = BaseEnMemoria()
        val local1 = local.guardarPendiente(p(0, "Local"))
        val local2 = local.guardarPendiente(p(0, "Local2"))
        local.reemplazarSincronizados(listOf(p(1, "Remoto")))
        assertTrue(local1.id < 0 && local2.id < local1.id)
        assertEquals(3, local.observar().first().size)
    }

    @Test fun sincronizarDosVecesNoDuplica() = runTest {
        val local = BaseEnMemoria(); val server = Servidor()
        val repo = ProductoRepositorioOffline(local, server)
        repo.registrar(p(0))
        repo.sincronizar()
        repo.sincronizar()
        assertEquals(1, repo.observar().first().size)
        assertEquals(1, server.posts)
        assertFalse(repo.observar().first().single().pendiente)
    }
}
