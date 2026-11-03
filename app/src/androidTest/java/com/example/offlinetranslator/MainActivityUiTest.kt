package com.example.offlinetranslator

import android.Manifest
import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUiTest {

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("offline_translator_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .putBoolean("pref_onboarding_shown", true)
            .commit()
    }

    @Test
    fun showsConfidenceTooltip() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.confidence_info_button)).perform(click())
            onView(withText(R.string.confidence_tooltip_title)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun emptyExportDoesNotOpenOptionsDialog() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.export_button)).perform(scrollTo(), click())
            onView(withText(R.string.export_dialog_title)).check(doesNotExist())
            onView(withId(R.id.app_title)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun showsModelManagerDialog() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.model_manager_button)).perform(scrollTo(), click())
            onView(withText(R.string.model_manager_title)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun favoritesToggleUpdatesLabel() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.favorites_button)).perform(scrollTo(), click())
            onView(withText(R.string.favorites_on_label)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun showsPhrasebookDialog() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.phrasebook_button)).perform(scrollTo(), click())
            onView(withText(R.string.phrasebook_title)).check(matches(isDisplayed()))
        }
    }
}
