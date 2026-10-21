package pe.edu.upeu.pharmamobil.domain.repository

import pe.edu.upeu.pharmamobil.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

interface ProductoRepository {
    /** La UI observa la base local. La implementación por defecto sirve a los dobles antiguos. */
    fun observar(): Flow<List<Producto>> = flow { emit(listar()) }

    /** Subir pendientes antes de descargar. */
    suspend fun sincronizar() = Unit

    /** Instante persistido de la última sincronización correcta, en milisegundos UTC. */
    fun observarUltimaSincronizacion(): Flow<Long?> = flowOf(null)


    /** Incorpora el producto al inventario y devuelve el producto ya identificado. */
    suspend fun registrar(producto: Producto): Producto

    /** Entrega el inventario completo en el orden en que fue registrado. */
    suspend fun listar(): List<Producto>

    /** Busca un producto por su identificador. */
    suspend fun obtenerPorId(id: Long): Producto

    /** Actualiza el producto indicado por su identificador. */
    suspend fun actualizar(producto: Producto): Producto

    /** Elimina el producto indicado por su identificador. */
    suspend fun eliminar(id: Long)
}
