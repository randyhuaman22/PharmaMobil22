package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android

internal actual fun crearHttpClientDePlataforma(
    configuracion: HttpClientConfig<*>.() -> Unit
): HttpClient = HttpClient(Android) {
    configuracion()
}
