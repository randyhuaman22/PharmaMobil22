package pe.edu.upeu.pharmamobil.presentation.producto

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pe.edu.upeu.pharmamobil.data.repository.FakeProductoRepository
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.usecase.ListarProductosUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CompartirProductoTest {
    @Test
    fun comparteElProductoSeleccionadoConSuPrecioYStock() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        try {
            val producto = Producto(7, "Ibuprofeno", 24.5, 8)
            val repo = FakeProductoRepository(mutableListOf(producto))
            var textoCapturado = ""
            val fake = object : Compartidor {
                override fun compartir(texto: String) { textoCapturado = texto }
            }
            val vm = ProductoViewModel(
                RegistrarProductoUseCase(repo),
                ListarProductosUseCase(repo),
                fake
            )
            vm.compartirProducto(7)
            assertTrue(textoCapturado.contains("Ibuprofeno"))
            assertTrue(textoCapturado.contains("Stock: 8"))
            assertTrue(textoCapturado.contains("24"))
        } finally {
            Dispatchers.resetMain()
        }
    }
}
