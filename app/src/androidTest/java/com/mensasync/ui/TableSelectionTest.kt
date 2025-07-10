package com.mensasync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mensasync.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.test.performTouchInput

@RunWith(AndroidJUnit4::class)
class TableSelectionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun tableSelection_andMenuActions_workAsExpected() {
        val name = "Max"
        val mapNode = composeTestRule.onNodeWithTag("mapArea")

        // Name eingeben + Verbinden
        composeTestRule
            .onNodeWithTag("startNameInput")
            .performTextInput(name)

        composeTestRule
            .onNodeWithTag("connectButton")
            .performClick()

        composeTestRule.onNodeWithTag("table_0").performClick()
        composeTestRule.onNodeWithText("Dieser Tisch ist frei.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hier hinsetzen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Schließen").performClick()

        // Tisch 1: Belegt von TestUser → Button "Tisch freigeben"
        composeTestRule.onNodeWithTag("table_1").performClick()
        composeTestRule.onNodeWithText("Du sitzt an diesem Tisch.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tisch freigeben").assertIsDisplayed()
        composeTestRule.onNodeWithText("Schließen").performClick()

        // Tisch 2: Belegt von anderen → Kein Button zum Hinsetzen
        composeTestRule.onNodeWithTag("table_2").performClick()
        composeTestRule.onNodeWithText("Anna").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hier hinsetzen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tisch freigeben").assertDoesNotExist()
        composeTestRule.onNodeWithText("Schließen").performClick()

        // Menü öffnen
        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .performClick()

        // Menüpunkt "Status senden" auswählen
        composeTestRule
            .onNodeWithText("Status senden")
            .performClick()

        // Menü wieder öffnen
        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .performClick()

        // Menüpunkt "Name ändern" auswählen
        composeTestRule
            .onNodeWithText("Name ändern")
            .performClick()

        // Neuer Name eingeben im Dialog
        composeTestRule
            .onNodeWithTag("renameInput")
            .performTextClearance()

        composeTestRule
            .onNodeWithTag("renameInput")
            .performTextInput("Eva")

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        // Menü nochmal öffnen
        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .performClick()

        // Menüpunkt "Trennen und Beenden" klicken
        composeTestRule
            .onNodeWithText("Trennen und Beenden")
            .performClick()

        composeTestRule.onNodeWithText("Person suchen").performTextInput("Lisa")

        // Lisa's Tische sollen hervorgehoben sein
        //composeTestRule.onNodeWithTag("table-8-highlighted").assertIsDisplayed()

        // ↔ Pan nach rechts
        mapNode.performTouchInput {
            swipe(
                start = center,
                end = center.copy(x = center.x + 200),
                durationMillis = 500
            )
        }

        // ↕ Pan nach unten
        mapNode.performTouchInput {
            swipe(
                start = center,
                end = center.copy(y = center.y + 200),
                durationMillis = 500
            )
        }

        // ↔ Pan nach links
        mapNode.performTouchInput {
            swipe(
                start = center,
                end = center.copy(x = center.x - 200),
                durationMillis = 500
            )
        }

        // ↕ Pan nach oben
        mapNode.performTouchInput {
            swipe(
                start = center,
                end = center.copy(y = center.y - 200),
                durationMillis = 500
            )
        }



    }

}


