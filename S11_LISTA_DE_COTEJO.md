# Guía Práctica N.º 11 — Lista de cotejo (evaluación honesta)

| Nº | Criterio de la guía | Estado tras edición | Archivo/prueba de verificación |
|---|---|---|---|
| 1 | El esquema SQLDelight compila y genera PharmaDb. | **Código configurado; compilación pendiente.** | `shared/src/commonMain/sqldelight/.../Producto.sq`; Gradle |
| 2 | Driver registrado en Android, iOS y pruebas. | **Configurado en fuentes; prueba pendiente.** | `CreadorDeDriverAndroid`, `CreadorDeDriverIos`, test JDBC AndroidHostTest |
| 3 | Capa local sin Ktor ni DTO. | **Comprobado estáticamente.** | `data/local/ProductoLocal.kt` |
| 4 | Repositorio con observar(): Flow y sincronizar(). | **Implementado en código.** | `domain/repository/ProductoRepository.kt`, `ProductoRepositorioOffline.kt` |
| 5 | ViewModel observa Flow y no recarga a mano. | **Implementado en código.** | `presentation/producto/ProductoViewModel.kt` |
| 6 | Lista con backend apagado. | **Pendiente de prueba real.** | Emulador Android/iOS + base sincronizada |
| 7 | Registro offline marcado pendiente. | **Implementado; pendiente de prueba real.** | `ProductoLocal`, interfaz «Pendiente» |
| 8 | Subir pendientes antes de bajar. | **Comprobado en código.** | `ProductoRepositorioOffline.sincronizar()` |
| 9 | Producto en PharmaSoft después de reconectar. | **No acreditable con este ZIP** (usa EscuelaJS, no PharmaSoft/JWT). | Requiere backend PharmaSoft y sesión 10 |
| 10 | Tres pruebas de commonTest pasando. | **7 tests escritos; ejecución no verificada.** | `ProductoRepositorioOfflineTest`, `ProductoApiMockEngineTest` |

**No incluir evidencias ficticias.** No se dispone de capturas de emulador, salida de tests Gradle, sesión JWT ni confirmación en Swagger de PharmaSoft. Ver `S11_REVISION_Y_EVIDENCIAS.md`.
