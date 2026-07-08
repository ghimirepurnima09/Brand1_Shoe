package com.example.brand_shoe.Model

data class ProductModel(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val stock: Int = 0
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "price" to price,
            "description" to description,
            "imageUrl" to imageUrl,
            "stock" to stock
        )
    }
}
