package pe.edu.upeu.pharmamobil.data.repository

import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.data.remote.ProductoRemoteDataSource
import pe.edu.upeu.pharmamobil.data.mapper.aDominio
import pe.edu.upeu.pharmamobil.data.mapper.aRequestDto

/** Adaptador remoto al backend presente en el ZIP (EscuelaJS, NO PharmaSoft/JWT). */
interface ProductoApi {
    suspend fun listar(): List<Producto>
    suspend fun registrar(producto: Producto): Producto
    suspend fun actualizar(producto: Producto): Producto
    suspend fun eliminar(id: Long)
    suspend fun obtener(id: Long): Producto
}

class ProductoApiKtor(private val remoto: ProductoRemoteDataSource) : ProductoApi {
    override suspend fun listar() = remoto.listar().map { it.aDominio() }
    override suspend fun registrar(producto: Producto) = remoto.registrar(producto.aRequestDto()).aDominio()
    override suspend fun actualizar(producto: Producto) = remoto.actualizar(producto.id, producto.aRequestDto()).aDominio()
    override suspend fun eliminar(id: Long) = remoto.eliminar(id)
    override suspend fun obtener(id: Long) = remoto.obtenerPorId(id).aDominio()
}
