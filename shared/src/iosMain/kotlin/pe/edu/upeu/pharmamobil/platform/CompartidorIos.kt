package pe.edu.upeu.pharmamobil.platform

import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

class CompartidorIos : Compartidor {
    override fun compartir(texto: String) {
        val hoja = UIActivityViewController(
            activityItems = listOf(texto),
            applicationActivities = null
        )
        // Elegimos el controlador visible; evita presentar desde uno ya cubierto.
        var presentador = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
        while (true) {
            val siguiente = presentador.presentedViewController ?: break
            presentador = siguiente
        }
        // En iPad, la hoja de actividades se presenta como popover.
        hoja.popoverPresentationController?.sourceView = presentador.view
        presentador.presentViewController(hoja, true, null)
    }
}
