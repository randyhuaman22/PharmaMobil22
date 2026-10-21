package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun crearHttpClient(): HttpClient = crearHttpClientDePlataforma {
    expectSuccess = true

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = ApiConfig.IGNORE_UNKNOWN_KEYS
                isLenient = true
                encodeDefaults = true
            }
        )
    }

    install(DefaultRequest) {
        url(ApiConfig.BASE_URL)
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }

    install(HttpTimeout) {
        requestTimeoutMillis = ApiConfig.REQUEST_TIMEOUT_MILLIS
        connectTimeoutMillis = ApiConfig.CONNECT_TIMEOUT_MILLIS
        socketTimeoutMillis = ApiConfig.SOCKET_TIMEOUT_MILLIS
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("KTOR: $message")
            }
        }
        level = LogLevel.BODY
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }
}

internal expect fun crearHttpClientDePlataforma(
    configuracion: HttpClientConfig<*>.() -> Unit
): HttpClient
