# S07 - Guia de documentacion REST

Este archivo contiene informacion lista para copiar al PDF de la Actividad Autonoma N.º 07. Los resultados observados, fechas, horas y capturas quedan como `PENDIENTE DE EJECUTAR` hasta probar la app en Android/iOS.

## A. Datos generales

| Dato | Valor |
| --- | --- |
| API | Platzi Fake Store API / EscuelaJS |
| Base URL | `https://api.escuelajs.co/api/v1/` |
| Version | `v1` |
| Recurso principal | `products` |
| Archivo de configuracion | `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/data/remote/ApiConfig.kt` |
| Cliente HTTP | Ktor Client multiplataforma |
| Motor Android | Ktor Android |
| Motor iOS | Ktor Darwin |

## B. Catalogo de endpoints

| Metodo | Ruta | Parametros | Respuesta esperada | Codigos de error |
| --- | --- | --- | --- | --- |
| GET | `/products` | `offset`, `limit` opcionales | 200 - arreglo de productos | 500 |
| GET | `/products/{id}` | `id` en ruta | 200 - un producto | 400, 404 |
| POST | `/products` | cuerpo JSON compatible con `ProductoRequestDto` | 201 - producto creado | 400, 401 |
| PUT | `/products/{id}` | `id` en ruta + cuerpo JSON compatible con `ProductoRequestDto` | 200 - producto actualizado | 400, 404 |
| DELETE | `/products/{id}` | `id` en ruta | 200 - confirmacion de eliminacion | 401, 404 |

## C. Diccionario DTO

### ProductoDto

| Campo JSON | Tipo Kotlin | Obligatorio | Valor por defecto | Campo en dominio |
| --- | --- | --- | --- | --- |
| `id` | `Int` | No | `0` | `Producto.id` como `Long` |
| `title` | `String` | No | `""` | `Producto.nombre` |
| `price` | `Double` | No | `0.0` | `Producto.precio` |
| `description` | `String` | No | `""` | No existe en dominio actual |
| `images` | `List<String>` | No | `emptyList()` | No existe en dominio actual |
| `category` | `CategoriaDto?` | No | `null` | No existe en dominio actual |

### CategoriaDto

| Campo JSON | Tipo Kotlin | Obligatorio | Valor por defecto | Campo en dominio |
| --- | --- | --- | --- | --- |
| `id` | `Int` | No | `0` | No existe en dominio actual |
| `name` | `String` | No | `""` | No existe en dominio actual |

### ProductoRequestDto

| Campo JSON | Tipo Kotlin | Obligatorio | Valor por defecto | Campo origen |
| --- | --- | --- | --- | --- |
| `title` | `String` | Si | Sin valor por defecto | `Producto.nombre` |
| `price` | `Double` | Si | Sin valor por defecto | `Producto.precio` |
| `description` | `String` | No | `"Producto registrado desde PharmaMobil"` | Valor controlado de la app |
| `categoryId` | `Int` | No | `1` | Categoria verificada en `/categories` |
| `images` | `List<String>` | No | `["https://placehold.co/600x400"]` | Imagen controlada de la app |

Nota: el modelo de dominio actual exige `stock`, pero EscuelaJS no devuelve un campo `stock`. El mapper usa `stock = 0` como valor local para no cambiar el dominio ni inventar ese campo en el DTO remoto.

## D. JSON real

Fragmento verificado contra `https://api.escuelajs.co/api/v1/products?offset=0&limit=1`:

```json
[
  {
    "id": 2,
    "title": "Classic Red Pullover Hoodie",
    "price": 10,
    "description": "Elevate your casual wardrobe with our Classic Red Pullover Hoodie.",
    "category": {
      "id": 1,
      "name": "Updated Category Name"
    },
    "images": [
      "https://i.imgur.com/1twoaDy.jpeg",
      "https://i.imgur.com/FDwQgLy.jpeg",
      "https://i.imgur.com/kg1ZhhH.jpeg"
    ]
  }
]
```

La respuesta real incluye mas campos como `slug`, `creationAt` y `updatedAt`. La app funciona porque Ktor usa `ignoreUnknownKeys = true`.

## E. Codigo DTO

```kotlin
@Serializable
data class ProductoDto(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val images: List<String> = emptyList(),
    val category: CategoriaDto? = null
)

@Serializable
data class CategoriaDto(
    val id: Int = 0,
    val name: String = ""
)

@Serializable
data class ProductoRequestDto(
    val title: String,
    val price: Double,
    val description: String = "Producto registrado desde PharmaMobil",
    val categoryId: Int = CategoriaDtoDefaults.CATEGORIA_ID,
    val images: List<String> = listOf(CategoriaDtoDefaults.IMAGEN_URL)
)
```

## F. Bitacora de pruebas

### PRUEBA 1 - GET exitoso

| Campo | Registro |
| --- | --- |
| Identificador | PRUEBA 1 |
| Nombre | GET exitoso |
| Fecha/hora | PENDIENTE DE EJECUTAR |
| Plataforma | PENDIENTE DE EJECUTAR |
| Pasos | Abrir Productos con conexion estable y observar logs `KTOR:`. |
| Resultado esperado | Codigo 200 y lista de productos renderizada. |
| Resultado observado | PENDIENTE DE EJECUTAR |
| Captura | PENDIENTE DE EJECUTAR |
| Que ve el usuario final | PENDIENTE DE EJECUTAR |
| Conclusion | PENDIENTE DE EJECUTAR |

### PRUEBA 2 - Recurso inexistente / 404

| Campo | Registro |
| --- | --- |
| Identificador | PRUEBA 2 |
| Nombre | Recurso inexistente / 404 |
| Fecha/hora | PENDIENTE DE EJECUTAR |
| Plataforma | PENDIENTE DE EJECUTAR |
| Pasos | Solicitar temporalmente `GET /products/999999`. |
| Resultado esperado | `ClientRequestException` con codigo 404 y mensaje controlado. |
| Resultado observado | PENDIENTE DE EJECUTAR |
| Captura | PENDIENTE DE EJECUTAR |
| Que ve el usuario final | PENDIENTE DE EJECUTAR |
| Conclusion | PENDIENTE DE EJECUTAR |

### PRUEBA 3 - Sin conexion

| Campo | Registro |
| --- | --- |
| Identificador | PRUEBA 3 |
| Nombre | Sin conexion |
| Fecha/hora | PENDIENTE DE EJECUTAR |
| Plataforma | PENDIENTE DE EJECUTAR |
| Pasos | Activar modo avion antes de abrir Productos o antes de reintentar. |
| Resultado esperado | Excepcion de entrada/salida capturada y mensaje entendible. |
| Resultado observado | PENDIENTE DE EJECUTAR |
| Captura | PENDIENTE DE EJECUTAR |
| Que ve el usuario final | PENDIENTE DE EJECUTAR |
| Conclusion | PENDIENTE DE EJECUTAR |

### PRUEBA 4 - Timeout

| Campo | Registro |
| --- | --- |
| Identificador | PRUEBA 4 |
| Nombre | Tiempo de espera agotado |
| Fecha/hora | PENDIENTE DE EJECUTAR |
| Plataforma | PENDIENTE DE EJECUTAR |
| Pasos | Cambiar temporalmente `ApiConfig.REQUEST_TIMEOUT_MILLIS` a `1`, ejecutar la peticion y restaurar `15_000`. |
| Resultado esperado | `HttpRequestTimeoutException` controlada; la interfaz no se congela. |
| Resultado observado | PENDIENTE DE EJECUTAR |
| Captura | PENDIENTE DE EJECUTAR |
| Que ve el usuario final | PENDIENTE DE EJECUTAR |
| Conclusion | PENDIENTE DE EJECUTAR |

### PRUEBA 5 - JSON con campo desconocido

| Campo | Registro |
| --- | --- |
| Identificador | PRUEBA 5 |
| Nombre | Campo desconocido en JSON |
| Fecha/hora | PENDIENTE DE EJECUTAR |
| Plataforma | PENDIENTE DE EJECUTAR |
| Pasos | Probar con `ApiConfig.IGNORE_UNKNOWN_KEYS = true`; luego cambiar temporalmente a `false` y restaurar a `true`. |
| Resultado esperado | Con `true`, la app ignora campos extra; con `false`, puede producirse `SerializationException`. |
| Resultado observado | PENDIENTE DE EJECUTAR |
| Captura | PENDIENTE DE EJECUTAR |
| Que ve el usuario final | PENDIENTE DE EJECUTAR |
| Conclusion | PENDIENTE DE EJECUTAR |

## Evidencias pendientes para el PDF

- Captura del registro `KTOR:` con peticion y respuesta.
- Captura de la app ejecutandose en emulador Android.
- Captura de la app ejecutandose en simulador iOS.
- Captura del registro de POST `/products`.
- Capturas de los cinco escenarios de prueba.
