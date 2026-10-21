package pe.edu.upeu.pharmamobil.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductoDto(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val images: List<String> = emptyList(),
    val category: CategoriaDto? = null
)

@Serializable
data class CategoriaDto(
    val id: Int = 0,
    val name: String = ""
)

@Serializable
data class ProductoRequestDto(
    val title: String,
    val price: Double,
    val description: String = "Producto registrado desde PharmaMobil",
    val categoryId: Int = CategoriaDtoDefaults.CATEGORIA_ID,
    val images: List<String> = listOf(CategoriaDtoDefaults.IMAGEN_URL)
)

object CategoriaDtoDefaults {
    const val CATEGORIA_ID = 1
    const val IMAGEN_URL = "https://placehold.co/600x400"
}
