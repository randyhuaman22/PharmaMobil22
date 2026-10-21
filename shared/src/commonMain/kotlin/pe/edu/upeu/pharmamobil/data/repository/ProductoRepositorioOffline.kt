package pe.edu.upeu.pharmamobil.data.repository

import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pe.edu.upeu.pharmamobil.data.local.ProductoAlmacen
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

/** Los productos que observa la interfaz SIEMPRE vienen del almacenamiento local. */
class ProductoRepositorioOffline(
    private val local: ProductoAlmacen,
    private val remoto: ProductoApi,
    private val ahora: () -> Long = { Clock.System.now().toEpochMilliseconds() }
) : ProductoRepository {
    private val mutex = Mutex()

    override fun observar(): Flow<List<Producto>> = local.observar()
    override fun observarUltimaSincronizacion(): Flow<Long?> = local.observarUltimaSincronizacion()

    override suspend fun listar(): List<Producto> = local.observar().first()

    override suspend fun obtenerPorId(id: Long): Producto =
        local.obtener(id) ?: remoto.obtener(id)

    /** Primero persiste; si la red falla el registro NUNCA se pierde. */
    override suspend fun registrar(producto: Producto): Producto {
        val guardado = local.guardarPendiente(producto)
        try {
            sincronizar()
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (_: Exception) {
            // Sin servidor, error HTTP o timeout: queda pendiente en SQLite.
        }
        return guardado
    }

    /** Actualizaciones y borrados mantienen el comportamiento remoto de la versión 09. */
    override suspend fun actualizar(producto: Producto): Producto {
        val actualizado = remoto.actualizar(producto)
        local.guardarActualizacion(actualizado)
        return actualizado
    }

    override suspend fun eliminar(id: Long) {
        // Producto aún local: nunca existió en el servidor.
        if (id < 0L) {
            local.eliminar(id)
        } else {
            remoto.eliminar(id)
            local.eliminar(id)
        }
    }

    override suspend fun sincronizar() {
        mutex.withLock {
            // Orden fundamental: subir primero; nunca borrar pendientes desde la bajada.
            local.pendientes().forEach { pendiente ->
                val confirmado = remoto.registrar(pendiente)
                // EscuelaJS omite stock en la respuesta; preservarlo localmente.
                val conStock = if (confirmado.stock == 0 && pendiente.stock > 0)
                    confirmado.copy(stock = pendiente.stock) else confirmado
                local.confirmarPendiente(pendiente.id, conStock)
            }
            val delServidor = remoto.listar()
            local.reemplazarSincronizados(delServidor)
            local.guardarUltimaSincronizacion(ahora())
        }
    }
}
