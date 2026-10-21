package pe.edu.upeu.pharmamobil.platform

/** Contrato común para mostrar la versión real del sistema operativo. */
expect class InfoDispositivo() {
    val sistema: String
    val version: String
}
