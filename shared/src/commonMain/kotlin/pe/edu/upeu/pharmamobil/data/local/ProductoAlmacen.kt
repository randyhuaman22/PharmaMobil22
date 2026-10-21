package pe.edu.upeu.pharmamobil.data.local

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobil.domain.model.Producto

/** Contrato local que permite probar la sincronización desde commonTest sin JDBC ni Android. */
interface ProductoAlmacen {
    fun observar(): Flow<List<Producto>>
    fun pendientes(): List<Producto>
    fun obtener(id: Long): Producto?
    fun guardarPendiente(producto: Producto): Producto
    fun confirmarPendiente(idLocal: Long, productoRemoto: Producto)
    fun reemplazarSincronizados(productos: List<Producto>)
    fun guardarActualizacion(producto: Producto)
    fun eliminar(id: Long)
    fun observarUltimaSincronizacion(): Flow<Long?>
    fun guardarUltimaSincronizacion(instante: Long)
}
