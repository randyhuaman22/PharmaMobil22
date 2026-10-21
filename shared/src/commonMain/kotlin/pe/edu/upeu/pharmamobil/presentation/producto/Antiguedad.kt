package pe.edu.upeu.pharmamobil.presentation.producto

import kotlin.time.Clock

/** Human-readable age; also handles the important no-successful-sync state. */
fun textoAntiguedad(ultima: Long?, ahora: Long = Clock.System.now().toEpochMilliseconds()): String {
    if (ultima == null) return "Nunca sincronizado"
    val delta = (ahora - ultima).coerceAtLeast(0L)
    val minutos = delta / 60_000
    val horas = delta / 3_600_000
    val dias = delta / 86_400_000
    return when {
        minutos < 1 -> "Actualizado hace menos de un minuto"
        minutos < 60 -> "Actualizado hace $minutos ${if (minutos == 1L) "minuto" else "minutos"}"
        horas < 24 -> "Actualizado hace $horas ${if (horas == 1L) "hora" else "horas"}"
        else -> "Actualizado hace $dias ${if (dias == 1L) "día" else "días"}"
    }
}
