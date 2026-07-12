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
class AdminLoginContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screen_displaysTitle() {
        composeTestRule.setContent {
            AdminLoginContent(onAdminLoginSuccess = {}, onBackClick = {})
        }
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithText("Admin Login").assertExists()
        composeTestRule.onNodeWithText("Staff access only").assertExists()
    }

    @Test
    fun enteringAdminEmail_showsTypedValue() {
        composeTestRule.setContent {
            AdminLoginContent(onAdminLoginSuccess = {}, onBackClick = {})
        }
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithText("Admin Email").performTextInput("admin@test.com")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("admin@test.com").assertExists()
    }

    @Test
    fun clickingBackToCustomerLogin_triggersCallback() {
        var backClicked = false

        composeTestRule.setContent {
            AdminLoginContent(onAdminLoginSuccess = {}, onBackClick = { backClicked = true })
        }
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        composeTestRule.onNodeWithText("Back to customer login").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.runOnIdle {
            assert(backClicked)
        }
    }
}