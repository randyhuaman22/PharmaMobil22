# Sesión 09 — Estado de la reconstrucción

Esta carpeta contiene el proyecto original PharmaMobilFinal-master actualizado con:

- `formatearSoles` en `commonMain`, `androidMain` e `iosMain`, conectado con `ProductoUi`.
- Interfaz `Compartidor`, implementaciones Android (`Intent`) e iOS (`UIActivityViewController`), módulos Koin actualizados y botón **Compartir** dentro del detalle de cada producto.
- `InfoDispositivo` con `expect/actual` y pantalla **Acerca de** accesible desde el menú.
- Permiso Android `INTERNET`.
- Prueba de doble de `Compartidor` y prueba de moneda adaptada a las diferencias de locale.

## Verificaciones realizadas

- Revisión de archivos `expect/actual` y registro de las dos implementaciones de `Compartidor` en Koin.
- No se encontraron `import android.` o `import platform.` en `shared/src/commonMain/kotlin`.
- No se borró el backend EscuelaJS ni la estructura anterior de los módulos.

## Limitaciones (no sustituir por evidencias inventadas)

- La compilación no pudo completarse en este entorno: la distribución Gradle 9.1.0 no estaba instalada y su URL de descarga no era accesible (`java.net.UnknownHostException: services.gradle.org`).
- No se ejecutó un emulador Android ni un simulador iOS; las capturas quedan pendientes.
- La parte iOS debe probarse con una Mac y Xcode.
- No se provocó un error literal por eliminación de `actual` en un target, porque no pudo iniciarse la compilación; capturar dicho error en el equipo de desarrollo y luego restaurar el `actual`.
- No se incluye historial Git ni commits, porque el ZIP original no contenía `.git`.

## Comandos para validar

En Windows desde Android Studio, abrir el directorio raíz del proyecto. Ejecutar:

```powershell
.\gradlew.bat :androidApp:assembleDebug
.\gradlew.bat :shared:testAndroidHostTest
```

En macOS, abrir `iosApp/iosApp.xcodeproj` en Xcode y ejecutar el simulador. Comparar las mismas funciones en ambas plataformas.

## Evidencias para entregar

1. Captura del precio del mismo producto en Android e iOS.
2. Captura del selector `Compartir` en ambos sistemas con el texto visible.
3. Captura de la pantalla «Acerca de» en ambos sistemas.
4. Captura del mensaje real del compilador al faltar un `actual`.
5. Capturas/rutas de commits, repositorio y README actualizado.

El informe Word de la actividad autónoma es un archivo separado de este ZIP y requiere insertar capturas y observaciones reales para estar terminado.
