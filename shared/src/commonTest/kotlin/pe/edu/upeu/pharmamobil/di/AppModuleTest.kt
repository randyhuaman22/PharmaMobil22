package pe.edu.upeu.pharmamobil.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import pe.edu.upeu.pharmamobil.data.remote.ProductoRemoteDataSource
import pe.edu.upeu.pharmamobil.data.repository.ProductoApi
import pe.edu.upeu.pharmamobil.data.local.ProductoAlmacen
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.repository.ClienteRepositorioEnMemoria
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioOffline
import pe.edu.upeu.pharmamobil.domain.repository.ClienteRepository
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.ActualizarProductoUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.EliminarProductoUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ListarClientesUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ListarProductosUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ObtenerProductoUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarClienteUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase
import pe.edu.upeu.pharmamobil.presentation.cliente.ClienteViewModel
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertSame

/**
 * Comprueba que el grafo se ensambla sin arrancar la aplicacion: si una
 * definicion falta o esta declarada con el tipo equivocado, falla aqui.
 *
 * Se cargan los cuatro modulos, incluido presentationModule: construir un
 * ViewModel es justo lo que se rompe al cambiar un constructor, y antes era
 * lo unico que el grafo no cubria. Como los ViewModel arrancan una carga en
 * su init, hace falta un Dispatchers.Main de prueba.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AppModuleTest {

    @BeforeTest
    fun instalarMain() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun detenerKoin() {
        stopKoin()
        Dispatchers.resetMain()
    }

    // Los dobles aíslan las pruebas de DI del Android Context, SQLite y la red externa.
    private val dobles = module {
        single<ProductoAlmacen> {
            object : ProductoAlmacen {
                private val productos = MutableStateFlow<List<Producto>>(emptyList())
                override fun observar(): Flow<List<Producto>> = productos
                override fun pendientes() = emptyList<Producto>()
                override fun obtener(id: Long) = null
                override fun guardarPendiente(producto: Producto) = producto
                override fun confirmarPendiente(idLocal: Long, productoRemoto: Producto) = Unit
                override fun reemplazarSincronizados(productos: List<Producto>) { this.productos.value = productos }
                override fun guardarActualizacion(producto: Producto) = Unit
                override fun eliminar(id: Long) = Unit
                override fun observarUltimaSincronizacion(): Flow<Long?> = MutableStateFlow(null)
                override fun guardarUltimaSincronizacion(instante: Long) = Unit
            }
        }
        single<ProductoApi> {
            object : ProductoApi {
                override suspend fun listar() = emptyList<Producto>()
                override suspend fun registrar(producto: Producto) = producto
                override suspend fun actualizar(producto: Producto) = producto
                override suspend fun eliminar(id: Long) = Unit
                override suspend fun obtener(id: Long): Producto = error("Sin producto")
            }
        }
        single<Compartidor> { object : Compartidor { override fun compartir(texto: String) = Unit } }
    }

    private fun grafoCompleto(): Koin = startKoin {
        modules(dataModule, domainModule, presentationModule, platformModule, dobles)
    }.koin

    @Test
    fun resuelveLosRepositoriosPorSuInterfazDeDominio() {

        val koin = grafoCompleto()

        assertIs<ProductoRepositorioOffline>(koin.get<ProductoRepository>())
        assertIs<ClienteRepositorioEnMemoria>(koin.get<ClienteRepository>())
    }

    @Test
    fun losRepositoriosSonUnicosEnTodaLaAplicacion() {

        val koin = grafoCompleto()

        assertSame(
            koin.get<ProductoRepository>(),
            koin.get<ProductoRepository>()
        )
        assertSame(
            koin.get<ClienteRepository>(),
            koin.get<ClienteRepository>()
        )
    }

    @Test
    fun resuelveLosCasosDeUsoConSusRepositorios() {

        val koin = grafoCompleto()

        koin.get<RegistrarProductoUseCase>()
        koin.get<ListarProductosUseCase>()
        koin.get<ObtenerProductoUseCase>()
        koin.get<ActualizarProductoUseCase>()
        koin.get<EliminarProductoUseCase>()
        koin.get<RegistrarClienteUseCase>()
        koin.get<ListarClientesUseCase>()
    }

    @Test
    fun resuelveLaCapaRemotaDeProductos() {

        val koin = grafoCompleto()

        koin.get<ProductoRemoteDataSource>()
    }

    @Test
    fun resuelveLosViewModelConSusCasosDeUso() {

        val koin = grafoCompleto()

        koin.get<ProductoViewModel>()
        koin.get<ClienteViewModel>()
    }
}
