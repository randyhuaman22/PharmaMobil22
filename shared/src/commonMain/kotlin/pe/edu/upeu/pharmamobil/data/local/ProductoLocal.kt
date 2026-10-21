package pe.edu.upeu.pharmamobil.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upeu.pharmamobil.db.PharmaDb
import pe.edu.upeu.pharmamobil.domain.model.Producto

/** Única fuente de productos para la UI. No depende de Ktor ni de DTO remotos. */
class ProductoLocal(private val base: PharmaDb) : ProductoAlmacen {
    private val consultas = base.productoQueries

    override fun observar(): Flow<List<Producto>> = consultas.listarTodos()
        .asFlow().mapToList(Dispatchers.Default)
        .map { filas -> filas.map { fila -> fila.aDominio() } }

    override fun pendientes(): List<Producto> = consultas.listarPendientes()
        .executeAsList().map { it.aDominio() }

    override fun obtener(id: Long): Producto? = consultas.buscarPorId(id)
        .executeAsOneOrNull()?.aDominio()

    override fun guardarPendiente(producto: Producto): Producto {
        var resultado: Producto? = null
        consultas.transaction {
            val menor = consultas.siguienteIdLocal().executeAsOne()
            val local = producto.copy(id = minOf(menor, 0L) - 1L, pendiente = true)
            consultas.insertar(local.id, local.nombre, local.precio, local.stock.toLong(), 1L)
            resultado = local
        }
        return requireNotNull(resultado)
    }

    /** Confirma el POST ANTES del GET para no reenviar al reconectar tras una bajada fallida. */
    override fun confirmarPendiente(idLocal: Long, productoRemoto: Producto) {
        consultas.transaction {
            consultas.borrarPorId(idLocal)
            val p = productoRemoto.copy(pendiente = false)
            consultas.insertar(p.id, p.nombre, p.precio, p.stock.toLong(), 0L)
        }
    }

    override fun reemplazarSincronizados(productos: List<Producto>) {
        consultas.transaction {
            // EscuelaJS no envía stock: conservar el último valor local conocido.
            // Al migrar a PharmaSoft, el mapper del servidor entregará el stock real.
            val stockLocal = consultas.listarTodos().executeAsList()
                .associate { fila -> fila.id to fila.stock }
            consultas.borrarSincronizados()
            productos.forEach {
                val stock = if (it.stock == 0 && (stockLocal[it.id] ?: 0L) > 0L)
                    stockLocal.getValue(it.id) else it.stock.toLong()
                consultas.insertar(it.id, it.nombre, it.precio, stock, 0L)
            }
        }
    }

    override fun guardarActualizacion(producto: Producto) {
        consultas.insertar(producto.id, producto.nombre, producto.precio,
            producto.stock.toLong(), if (producto.pendiente) 1L else 0L)
    }

    override fun eliminar(id: Long) { consultas.borrarPorId(id) }

    override fun observarUltimaSincronizacion(): Flow<Long?> = consultas.ultimaSincronizacion()
        .asFlow().mapToOneOrNull(Dispatchers.Default)

    override fun guardarUltimaSincronizacion(instante: Long) {
        consultas.guardarSincronizacion(instante)
    }

    private fun pe.edu.upeu.pharmamobil.db.Producto.aDominio(): Producto = Producto(
        id = id, nombre = nombre, precio = precio,
        stock = stock.toInt(), pendiente = pendiente == 1L
    )
}
