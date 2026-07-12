package com.example.brand_shoe

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForgetPasswordContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screen_displaysTitleAndSubtitle() {
        composeTestRule.setContent {
            ForgetPasswordContent(onBackClick = {})
        }

        composeTestRule.onNodeWithText("Forgot Password").assertExists()
        composeTestRule.onNodeWithText("Enter your email to receive a reset link").assertExists()
    }

    @Test
    fun enteringEmail_showsTypedValue() {
        composeTestRule.setContent {
            ForgetPasswordContent(onBackClick = {})
        }

        composeTestRule.onNodeWithText("Email Address").performTextInput("user@test.com")
        composeTestRule.onNodeWithText("user@test.com").assertExists()
    }

    @Test
    fun clickingBackToLogin_triggersCallback() {
        var backClicked = false

        composeTestRule.setContent {
            ForgetPasswordContent(onBackClick = { backClicked = true })
        }

        composeTestRule.onNodeWithText("Back to Log In").performClick()

        assert(backClicked)
    }
}