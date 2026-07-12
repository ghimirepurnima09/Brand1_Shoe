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
class LoginContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysWelcomeText() {
        composeTestRule.setContent {
            LoginContent(
                onNavigateHome = {},
                onNavigateAdmin = {},
                onRegisterClick = {},
                onForgetPasswordClick = {}
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Welcome Back").assertExists()
    }

    @Test
    fun enteringEmailAndPassword_updatesFields() {
        composeTestRule.setContent {
            LoginContent(
                onNavigateHome = {},
                onNavigateAdmin = {},
                onRegisterClick = {},
                onForgetPasswordClick = {}
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Email").performTextInput("test@mail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("123456")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("test@mail.com").assertExists()
    }

    @Test
    fun clickingRegisterLink_triggersCallback() {
        var registerClicked = false

        composeTestRule.setContent {
            LoginContent(
                onNavigateHome = {},
                onNavigateAdmin = {},
                onRegisterClick = { registerClicked = true },
                onForgetPasswordClick = {}
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Create Account").performClick()
        composeTestRule.waitForIdle()

        assert(registerClicked)
    }
}