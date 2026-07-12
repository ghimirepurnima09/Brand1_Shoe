package com.example.brand_shoe

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductFormValidatorTest {

    @Test
    fun blankName_returnsNameError() {
        val result = ProductFormValidator.validate("", "100", "5")
        assertEquals("Product name is required", result)
    }

    @Test
    fun invalidPrice_returnsPriceError() {
        val result = ProductFormValidator.validate("Shoe", "abc", "5")
        assertEquals("Enter a valid price", result)
    }

    @Test
    fun zeroPrice_returnsPriceError() {
        val result = ProductFormValidator.validate("Shoe", "0", "5")
        assertEquals("Enter a valid price", result)
    }

    @Test
    fun invalidStock_returnsStockError() {
        val result = ProductFormValidator.validate("Shoe", "100", "-1")
        assertEquals("Enter a valid stock quantity", result)
    }

    @Test
    fun validInput_returnsNull() {
        val result = ProductFormValidator.validate("Shoe", "100.0", "5")
        assertNull(result)
    }
}