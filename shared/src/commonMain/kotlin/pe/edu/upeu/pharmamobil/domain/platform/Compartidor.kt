package pe.edu.upeu.pharmamobil.domain.platform

/** Contrato compartido: presentation nunca conoce Intent ni UIKit. */
interface Compartidor {
    fun compartir(texto: String)
}
