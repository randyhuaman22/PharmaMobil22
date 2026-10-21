package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

/**
 * Doble del inventario para las pruebas: sin delay y capaz de fallar a
 * voluntad. Sin el, probar el camino de error del caso de uso o del ViewModel
 * era imposible, porque el repositorio en memoria nunca falla.
 */
class FakeProductoRepository(
    private val productos: MutableList<Producto> = mutableListOf()
) : ProductoRepository {

    var fallaAlRegistrar: Throwable? = null
    var fallaAlListar: Throwable? = null

    private var siguienteId = 1L
    private val estado = MutableStateFlow(productos.toList())
    override fun observar(): Flow<List<Producto>> = kotlinx.coroutines.flow.flow {
        fallaAlListar?.let { throw it }
        estado.collect { emit(it) }
    }

    override suspend fun registrar(producto: Producto): Producto {

        fallaAlRegistrar?.let { throw it }

        val guardado = producto.copy(id = siguienteId++)
        productos.add(guardado)
        estado.value = productos.toList()
        return guardado
    }

    override suspend fun listar(): List<Producto> {

        fallaAlListar?.let { throw it }

        return productos.toList()
    }

    override suspend fun obtenerPorId(id: Long): Producto {
        fallaAlListar?.let { throw it }
        return productos.firstOrNull { it.id == id }
            ?: error("Sin producto de prueba con id $id")
    }

    override suspend fun actualizar(producto: Producto): Producto {
        fallaAlRegistrar?.let { throw it }
        val indice = productos.indexOfFirst { it.id == producto.id }
        if (indice == -1) {
            error("Sin producto de prueba con id ${producto.id}")
        }
        productos[indice] = producto
        estado.value = productos.toList()
        return producto
    }

    override suspend fun eliminar(id: Long) {
        fallaAlRegistrar?.let { throw it }
        productos.removeAll { it.id == id }
        estado.value = productos.toList()
    }
}
