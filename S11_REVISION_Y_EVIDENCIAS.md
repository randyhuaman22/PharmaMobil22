# Sesión 11 — implementación y limitaciones de validación

**Estado:** código fuente integrado, NO compilado/ejecutado en este entorno. No se han generado capturas de emulador ni del servidor.

## Correspondencia con la guía

1. `gradle/libs.versions.toml` y `shared/build.gradle.kts`: SQLDelight 2.1.0, runtime, coroutines y drivers.
2. `shared/src/commonMain/sqldelight/.../Producto.sq`: esquema con `pendiente`, ids negativos y timestamp.
3. `data/local/`: `ProductoLocal`, `ProductoAlmacen`, `CreadorDeDriver` y drivers específicos Android/iOS.
4. `data/repository/ProductoRepositorioOffline.kt`: observa exclusivamente SQLite, sube pendientes y luego baja productos; confirma cada POST para evitar duplicación ante GET fallido.
5. `ProductoViewModel`: recibe el `Flow`, actualización automática, aviso de falla y botón de sincronizar.
6. `ProductoScreen`: muestra registros pendientes y antigüedad de datos.
7. Seis pruebas PORTABLES de sincronización en `commonTest/data/repository/ProductoRepositorioOfflineTest.kt`, más dos pruebas de SQLite/JDBC en `androidHostTest` y una prueba HTTP con MockEngine en `commonTest`.

## DIFERENCIAS REALES respecto al escenario del profesor

El ZIP base proviene de la Sesión 9 y **NO incluye** el login JWT ni el backend PharmaSoft de la Sesión 10. Consume **EscuelaJS** (`https://api.escuelajs.co/api/v1/products`), cuyos DTO no tienen `stock` ni contemplan JWT. Por eso, las capturas de Swagger de PharmaSoft, pruebas de 401/403, escenario de sesión caducada y autenticación siguen PENDIENTES hasta integrar la versión de la Sesión 10 y levantar PharmaSoft. No se inventaron estos componentes.

El CRUD de actualizar/eliminar se conserva remoto de la Sesión 9 (no se prometen cambios offline para esas dos operaciones). El registro funciona offline en SQLite.

El JDBC driver es JVM-only y no puede cargarse de forma correcta en un source set compartido con iOS. Se añadieron **seis tests multiplataforma** sobre un doble de almacenamiento local en `commonTest`, y **dos pruebas JDBC con SQLDelight real** en `androidHostTest`.

## Lo que falta validar en tu máquina

- Abrir en Android Studio y ejecutar Gradle Sync con acceso a Internet.
- `./gradlew :shared:generateCommonMainPharmaDbInterface` (el nombre de tarea puede variar con el plugin).
- `./gradlew :shared:testAndroidHostTest` y, en Mac, `./gradlew :shared:iosSimulatorArm64Test`.
- `./gradlew :androidApp:assembleDebug` y ejecutar Android en dispositivo/emulador.
- En Mac, compilar y ejecutar iosApp con Xcode.
- En Android: sincronizar con internet; cerrar app; activar modo avión; reabrir, registrar un producto; quitar modo avión; pulsar Sincronizar; verificar en backend de EscuelaJS con ID positivo. Esto NO sustituye la evidencia de PharmaSoft exigida por el docente.
- Reunir capturas reales en Android/iOS y el resultado real de la suite. No se fabrican evidencias.
- Si se pasa a PharmaSoft: adaptar `ApiConfig`, DTO, `ProductoApiKtor` y autenticar solicitudes mediante el token JWT real.

### Limitaciones de la práctica

La API EscuelaJS asigna stock local por defecto `0` en su mapper remoto. Al actualizar desde servidor, no se recupera stock real; para el criterio literal con PharmaSoft debe usarse el backend que sí modele el campo.

Las versiones iniciales Kotlin 2.4.10 / Android Gradle Plugin 9.0.1 del ZIP también requieren validación de compatibilidad con SQLDelight 2.1.0 en Gradle Sync.

## Comprobación realizada en este entorno

Se intentó `bash ./gradlew :shared:testAndroidHostTest --offline --no-daemon`. El wrapper no pudo descargar `gradle-9.1.0-bin.zip` debido a `java.net.UnknownHostException: services.gradle.org`. **No se ejecutó ninguna prueba ni se verificó compilación**. Se efectuaron comprobaciones estáticas de rutas y ausencia de imports nativos en commonMain.
