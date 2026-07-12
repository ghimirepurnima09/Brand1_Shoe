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
class RegistrationContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screen_displaysTitle() {
        composeTestRule.setContent {
            RegistrationContent(onRegisterSuccess = {}, onLoginClick = {})
        }

        composeTestRule.onNodeWithText("Create Account").assertExists()
    }

    @Test
    fun enteringName_showsTypedValue() {
        composeTestRule.setContent {
            RegistrationContent(onRegisterSuccess = {}, onLoginClick = {})
        }

        composeTestRule.onNodeWithText("Full Name").performTextInput("John Doe")
        composeTestRule.onNodeWithText("John Doe").assertExists()
    }

    @Test
    fun clickingLoginNow_triggersCallback() {
        var loginClicked = false

        composeTestRule.setContent {
            RegistrationContent(onRegisterSuccess = {}, onLoginClick = { loginClicked = true })
        }

        composeTestRule.onNodeWithText("Log In Now").performClick()

        assert(loginClicked)
    }
}