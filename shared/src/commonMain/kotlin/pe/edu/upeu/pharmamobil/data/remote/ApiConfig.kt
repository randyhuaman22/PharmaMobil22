package pe.edu.upeu.pharmamobil.data.remote

object ApiConfig {

    const val BASE_URL = "https://api.escuelajs.co/api/v1/"

    const val PRODUCTOS_PATH = "products"

    const val REQUEST_TIMEOUT_MILLIS = 15_000L
    const val CONNECT_TIMEOUT_MILLIS = 10_000L
    const val SOCKET_TIMEOUT_MILLIS = 15_000L

    // Para la prueba 5, cambiar temporalmente a false y restaurar despues.
    const val IGNORE_UNKNOWN_KEYS = true

    fun rutaProductoPorId(id: Long): String = "$PRODUCTOS_PATH/$id"
}
