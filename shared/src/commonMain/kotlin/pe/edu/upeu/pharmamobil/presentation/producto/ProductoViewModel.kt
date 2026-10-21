package pe.edu.upeu.pharmamobil.presentation.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.ListarProductosUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ProductoInvalidoException
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.comoTextoParaCompartir

class ProductoViewModel(
    private val registrarProducto: RegistrarProductoUseCase,
    @Suppress("unused") private val listarProductos: ListarProductosUseCase,
    private val compartidor: Compartidor,
    private val repositorio: ProductoRepository
) : ViewModel() {
    private var productosDominio: Map<Long, Producto> = emptyMap()
    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    init {
        // Se suscribe una sola vez. La lista se actualiza cuando la tabla cambia.
        viewModelScope.launch {
            try {
                repositorio.observar().collect { productos ->
                    productosDominio = productos.associateBy { it.id }
                    _uiState.update {
                        it.copy(fase = if (productos.isEmpty())
                            ProductoUiState.Fase.SinProductos
                        else ProductoUiState.Fase.ConProductos(productos.map { p -> p.aUi() }))
                    }
                }
            } catch (ex: Exception) {
                _uiState.update { it.copy(fase = ProductoUiState.Fase.Error(ex.message ?: "Error local")) }
            }
        }
        viewModelScope.launch {
            repositorio.observarUltimaSincronizacion().collect { tiempo ->
                _uiState.update { it.copy(ultimaSincronizacion = tiempo) }
            }
        }
        sincronizar()
    }

    /** Conserva el nombre anterior para el botón Reintentar, pero no recarga listas HTTP. */
    fun cargarProductos() = sincronizar()

    fun sincronizar() {
        viewModelScope.launch {
            _uiState.update { it.copy(sincronizando = true) }
            val fallo = try {
                repositorio.sincronizar()
                null
            } catch (ex: Exception) { ex }
            _uiState.update {
                it.copy(
                    sincronizando = false,
                    avisoRed = if (fallo == null) null else
                        "Sin conexión: mostrando datos guardados (${fallo.message ?: "sin respuesta"})"
                )
            }
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(formulario = it.formulario.copy(nombre = nombre, nombreError = null), mensajeExito = null) }
    }
    fun onPrecioChange(precio: String) {
        _uiState.update { it.copy(formulario = it.formulario.copy(precio = precio, precioError = null), mensajeExito = null) }
    }
    fun onStockChange(stock: String) {
        _uiState.update { it.copy(formulario = it.formulario.copy(stock = stock, stockError = null), mensajeExito = null) }
    }

    fun registrar() {
        if (_uiState.value.registrando) return
        viewModelScope.launch {
            _uiState.update { it.copy(registrando = true, mensajeExito = null) }
            val f = _uiState.value.formulario
            registrarProducto(f.nombre, f.precio, f.stock).fold(
                onSuccess = { producto ->
                    // La lista se actualiza SOLA por observar(), nunca la editamos a mano.
                    _uiState.update { it.copy(
                        registrando = false,
                        formulario = FormularioProducto(),
                        mensajeExito = "Producto guardado localmente; comprueba su estado de sincronización en la lista"
                    ) }
                },
                onFailure = { fallo ->
                    when (fallo) {
                        is ProductoInvalidoException -> _uiState.update { it.copy(
                            registrando = false,
                            formulario = it.formulario.copy(
                                nombreError = fallo.errores.nombre,
                                precioError = fallo.errores.precio,
                                stockError = fallo.errores.stock
                            )
                        ) }
                        else -> _uiState.update { it.copy(
                            registrando = false,
                            avisoRed = fallo.message ?: "Error al registrar el producto"
                        ) }
                    }
                }
            )
        }
    }

    fun compartirProducto(id: Long) {
        val producto = productosDominio[id] ?: return
        compartidor.compartir(producto.comoTextoParaCompartir())
    }
}
