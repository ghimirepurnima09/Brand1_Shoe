package com.example.brand_shoe

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RegistrationValidatorTest {

    @Test
    fun blankFields_returnsFillAllFieldsError() {
        val result = RegistrationValidator.validate("", "test@mail.com", "123456", "123456")
        assertEquals("Please fill all fields", result)
    }

    @Test
    fun mismatchedPasswords_returnsMismatchError() {
        val result = RegistrationValidator.validate("John", "test@mail.com", "abcdef", "abcxyz")
        assertEquals("Passwords do not match", result)
    }

    @Test
    fun shortPassword_returnsLengthError() {
        val result = RegistrationValidator.validate("John", "test@mail.com", "123", "123")
        assertEquals("Password must be at least 6 characters", result)
    }

    @Test
    fun validInput_returnsNull() {
        val result = RegistrationValidator.validate("John", "test@mail.com", "123456", "123456")
        assertNull(result)
    }
}