package com.example.offlinetranslator

import android.Manifest
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUiTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)

    @Test
    fun showsConfidenceTooltip() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.confidence_info_button)).perform(click())
        onView(withText(R.string.confidence_tooltip_title)).check(matches(isDisplayed()))
    }

    @Test
    fun showsExportOptionsDialog() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.export_button)).perform(click())
        onView(withText(R.string.export_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.export_option_text)).check(matches(isDisplayed()))
    }

    @Test
    fun showsModelManagerDialog() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.model_manager_button)).perform(click())
        onView(withText(R.string.model_manager_title)).check(matches(isDisplayed()))
    }

    @Test
    fun favoritesToggleUpdatesLabel() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.favorites_button)).perform(click())
        onView(withText(R.string.favorites_on_label)).check(matches(isDisplayed()))
    }

    @Test
    fun showsPhrasebookDialog() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.phrasebook_button)).perform(click())
        onView(withText(R.string.phrasebook_title)).check(matches(isDisplayed()))
    }
}
