package pe.edu.upeu.pharmamobil.domain.usecase

import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.platform.formatearSoles

/** El contenido del mensaje es compartido; solo la acción es nativa. */
fun Producto.comoTextoParaCompartir(): String =
    "$nombre — ${formatearSoles(precio)} · Stock: $stock"
