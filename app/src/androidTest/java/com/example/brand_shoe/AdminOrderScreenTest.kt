package com.example.brand_shoe

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminOrderScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screen_displaysTitleAndFilters() {
        composeTestRule.setContent {
            AdminOrderScreen(onBackClick = {})
        }

        composeTestRule.onNodeWithText("Manage Orders").assertExists()
        composeTestRule.onNodeWithText("All").assertExists()
        composeTestRule.onNodeWithText("Pending").assertExists()
    }

    @Test
    fun typingInSearchBar_showsTypedText() {
        composeTestRule.setContent {
            AdminOrderScreen(onBackClick = {})
        }

        composeTestRule.onNodeWithText("Search by customer or product...")
            .performTextInput("Nike")

        composeTestRule.onNodeWithText("Nike").assertExists()
    }
}