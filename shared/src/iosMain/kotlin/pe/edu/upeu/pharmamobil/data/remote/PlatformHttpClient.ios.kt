package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin

internal actual fun crearHttpClientDePlataforma(
    configuracion: HttpClientConfig<*>.() -> Unit
): HttpClient = HttpClient(Darwin) {
    configuracion()
}
