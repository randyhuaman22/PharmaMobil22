package pe.edu.upeu.pharmamobil.data.repository

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import pe.edu.upeu.pharmamobil.data.mapper.aDominio
import pe.edu.upeu.pharmamobil.data.mapper.aRequestDto
import pe.edu.upeu.pharmamobil.data.remote.ProductoRemoteDataSource
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

class ProductoRepositorioRest(
    private val remoteDataSource: ProductoRemoteDataSource
) : ProductoRepository {

    override suspend fun registrar(producto: Producto): Producto = ejecutarOperacionRemota {
        remoteDataSource.registrar(producto.aRequestDto()).aDominio()
    }

    override suspend fun listar(): List<Producto> = ejecutarOperacionRemota {
        remoteDataSource.listar().map { it.aDominio() }
    }

    override suspend fun obtenerPorId(id: Long): Producto = ejecutarOperacionRemota {
        remoteDataSource.obtenerPorId(id).aDominio()
    }

    override suspend fun actualizar(producto: Producto): Producto = ejecutarOperacionRemota {
        remoteDataSource.actualizar(producto.id, producto.aRequestDto()).aDominio()
    }

    override suspend fun eliminar(id: Long) {
        ejecutarOperacionRemota {
            remoteDataSource.eliminar(id)
        }
    }

    private suspend fun <T> ejecutarOperacionRemota(operacion: suspend () -> T): T {
        return try {
            operacion()
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (fallo: ClientRequestException) {
            throw ProductoRemotoException(fallo.mensajeCliente(), fallo)
        } catch (fallo: RedirectResponseException) {
            throw ProductoRemotoException(
                "La API respondio con una redireccion inesperada (${fallo.response.status.value}).",
                fallo
            )
        } catch (fallo: ServerResponseException) {
            throw ProductoRemotoException(
                "El servidor no pudo atender la solicitud (${fallo.response.status.value}).",
                fallo
            )
        } catch (fallo: HttpRequestTimeoutException) {
            throw ProductoRemotoException(
                "La solicitud tardo demasiado. Revisa la conexion e intenta otra vez.",
                fallo
            )
        } catch (fallo: IOException) {
            throw ProductoRemotoException(
                "No hay conexion con el servicio. Revisa internet o la URL configurada.",
                fallo
            )
        } catch (fallo: SerializationException) {
            throw ProductoRemotoException(
                "La respuesta del servidor no coincide con el DTO de productos.",
                fallo
            )
        }
    }

    private fun ClientRequestException.mensajeCliente(): String {
        return if (response.status == HttpStatusCode.NotFound) {
            "No se encontro el producto solicitado (404)."
        } else {
            "La API rechazo la solicitud (${response.status.value})."
        }
    }
}

class ProductoRemotoException(
    message: String,
    cause: Throwable
) : RuntimeException(message, cause)
