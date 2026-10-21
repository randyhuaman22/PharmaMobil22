package pe.edu.upeu.pharmamobil.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import pe.edu.upeu.pharmamobil.data.remote.ApiConfig
import pe.edu.upeu.pharmamobil.data.remote.KtorProductoRemoteDataSource
import kotlin.test.Test
import kotlin.test.assertEquals

/** Contrasta también el adaptador HTTP Ktor con MockEngine, sin utilizar red real. */
class ProductoApiMockEngineTest {
    @Test fun laApiMockLeeProductosRemotos() = runTest {
        val engine = MockEngine {
            respond(
                content = """[{"id":12,"title":"Ibuprofeno","price":4.5,"description":"Medicamento"}]""",
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest { url(ApiConfig.BASE_URL) }
        }
        try {
            val api = ProductoApiKtor(KtorProductoRemoteDataSource(client))
            val lista = api.listar()
            assertEquals(1, lista.size)
            assertEquals(12L, lista.single().id)
            assertEquals("Ibuprofeno", lista.single().nombre)
        } finally {
            client.close()
        }
    }
}
