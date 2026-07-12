package com.example.brand_shoe

object RegistrationValidator {

    /**
     * Returns an error message if invalid, or null if all fields pass.
     * Pulled out of RegistrationContent's onClick so it can be unit tested
     * without needing a UI/emulator.
     */
    fun validate(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            name.isBlank() || email.isBlank() || password.isBlank() ->
                "Please fill all fields"
            password != confirmPassword ->
                "Passwords do not match"
            password.length < 6 ->
                "Password must be at least 6 characters"
            else -> null
        }
    }
}