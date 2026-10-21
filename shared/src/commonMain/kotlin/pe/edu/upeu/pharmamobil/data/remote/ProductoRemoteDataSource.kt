package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoDto
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoRequestDto

interface ProductoRemoteDataSource {

    suspend fun listar(): List<ProductoDto>

    suspend fun obtenerPorId(id: Long): ProductoDto

    suspend fun registrar(producto: ProductoRequestDto): ProductoDto

    suspend fun actualizar(id: Long, producto: ProductoRequestDto): ProductoDto

    suspend fun eliminar(id: Long)
}

class KtorProductoRemoteDataSource(
    private val httpClient: HttpClient
) : ProductoRemoteDataSource {

    override suspend fun listar(): List<ProductoDto> {
        return httpClient.get(ApiConfig.PRODUCTOS_PATH).body()
    }

    override suspend fun obtenerPorId(id: Long): ProductoDto {
        return httpClient.get(ApiConfig.rutaProductoPorId(id)).body()
    }

    override suspend fun registrar(producto: ProductoRequestDto): ProductoDto {
        return httpClient.post(ApiConfig.PRODUCTOS_PATH) {
            contentType(ContentType.Application.Json)
            setBody(producto)
        }.body()
    }

    override suspend fun actualizar(id: Long, producto: ProductoRequestDto): ProductoDto {
        return httpClient.put(ApiConfig.rutaProductoPorId(id)) {
            contentType(ContentType.Application.Json)
            setBody(producto)
        }.body()
    }

    override suspend fun eliminar(id: Long) {
        httpClient.delete(ApiConfig.rutaProductoPorId(id))
    }
}
