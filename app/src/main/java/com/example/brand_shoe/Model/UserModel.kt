package com.example.brand_shoe.Model

data class UserModel(
    val id: String = "",
    val fName: String = "",
    val email: String = "",
    val address: String = "",
    val contact: String = "",
    val role: String = "customer" // "customer" or "admin"
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "fName" to fName,
            "email" to email,
            "address" to address,
            "contact" to contact,
            "role" to role
        )
    }
}