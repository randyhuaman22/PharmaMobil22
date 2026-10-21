package pe.edu.upeu.pharmamobil.data.mapper

import kotlinx.serialization.SerializationException
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoDto
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoRequestDto
import pe.edu.upeu.pharmamobil.domain.model.Producto

fun ProductoDto.aDominio(): Producto = Producto(
    id = id.toLong(),
    nombre = title.takeIf { it.isNotBlank() }
        ?: throw SerializationException("El producto remoto no incluye title valido."),
    precio = price.takeIf { it > 0 && it.isFinite() }
        ?: throw SerializationException("El producto remoto no incluye price valido."),
    stock = STOCK_LOCAL_POR_DEFECTO
)

fun Producto.aRequestDto(): ProductoRequestDto = ProductoRequestDto(
    title = nombre,
    price = precio
)

private const val STOCK_LOCAL_POR_DEFECTO = 0
