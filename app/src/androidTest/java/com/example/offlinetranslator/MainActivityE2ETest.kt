package com.example.offlinetranslator

import android.Manifest
import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end UI flows for Puente main screen.
 * Speech recognition / ML Kit translation are not exercised here because they
 * require live audio input and downloaded models on device/emulator.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityE2ETest {

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)

    @Before
    fun setUp() {
        seedPreferences(onboardingShown = true)
    }

    @Test
    fun coldStart_showsBrandingEmptyStateAndPrimaryControls() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.app_title))
                .check(matches(allOf(isDisplayed(), withText(R.string.app_title))))
            onView(withId(R.id.subtitle))
                .check(matches(allOf(isDisplayed(), withText(R.string.subtitle_auto))))
            onView(withId(R.id.empty_state_text))
                .check(matches(allOf(isDisplayed(), withText(R.string.empty_state))))
            onView(withId(R.id.status_text)).check(matches(isDisplayed()))
            onView(withId(R.id.toggle_listening_button))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
            onView(withId(R.id.clear_button))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
            onView(withId(R.id.language_toggle_button))
                .perform(scrollTo())
                .check(matches(withText(R.string.language_toggle_auto)))
        }
    }

    @Test
    fun onboarding_showsOnceThenPersistsDismissal() {
        seedPreferences(onboardingShown = false)

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withText(R.string.onboarding_title)).check(matches(isDisplayed()))
            onView(withText(R.string.onboarding_message)).check(matches(isDisplayed()))
            onView(withText(R.string.onboarding_cta)).perform(click())
            onView(withId(R.id.app_title)).check(matches(isDisplayed()))
        }

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.app_title)).check(matches(isDisplayed()))
            onView(withText(R.string.onboarding_title)).check(doesNotExist())
        }
    }

    @Test
    fun languageModeCycle_updatesToggleAndSubtitle() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.language_toggle_button)).perform(scrollTo(), click())
            onView(withId(R.id.language_toggle_button))
                .check(matches(withText(R.string.language_toggle_en_es)))
            onView(withId(R.id.subtitle))
                .check(matches(withText(R.string.subtitle_en_es)))

            onView(withId(R.id.language_toggle_button)).perform(click())
            onView(withId(R.id.language_toggle_button))
                .check(matches(withText(R.string.language_toggle_en_fr)))
            onView(withId(R.id.subtitle))
                .check(matches(withText(R.string.subtitle_en_fr)))

            onView(withId(R.id.language_toggle_button)).perform(click())
            onView(withId(R.id.language_toggle_button))
                .check(matches(withText(R.string.language_toggle_en_de)))
            onView(withId(R.id.subtitle))
                .check(matches(withText(R.string.subtitle_en_de)))

            onView(withId(R.id.language_toggle_button)).perform(click())
            onView(withId(R.id.language_toggle_button))
                .check(matches(withText(R.string.language_toggle_auto)))
            onView(withId(R.id.subtitle))
                .check(matches(withText(R.string.subtitle_auto)))
            onView(withId(R.id.status_text))
                .check(matches(withText(R.string.status_ready)))
        }
    }

    @Test
    fun settingsToggles_persistAcrossRelaunch() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.silence_toggle_switch)).perform(scrollTo(), click())
            onView(withId(R.id.noise_reduction_switch)).perform(scrollTo(), click())
            onView(withId(R.id.push_to_talk_switch)).perform(scrollTo(), click())

            onView(withId(R.id.silence_toggle_switch)).check(matches(isNotChecked()))
            onView(withId(R.id.noise_reduction_switch)).check(matches(isChecked()))
            onView(withId(R.id.push_to_talk_switch)).check(matches(isChecked()))
        }

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.silence_toggle_switch))
                .perform(scrollTo())
                .check(matches(isNotChecked()))
            onView(withId(R.id.noise_reduction_switch))
                .perform(scrollTo())
                .check(matches(isChecked()))
            onView(withId(R.id.push_to_talk_switch))
                .perform(scrollTo())
                .check(matches(isChecked()))
        }
    }

    @Test
    fun favoritesFilter_togglesLabelAndPersists() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.favorites_button))
                .perform(scrollTo())
                .check(matches(withText(R.string.favorites_label)))
            onView(withId(R.id.favorites_button)).perform(click())
            onView(withId(R.id.favorites_button))
                .check(matches(withText(R.string.favorites_on_label)))
        }

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.favorites_button))
                .perform(scrollTo())
                .check(matches(withText(R.string.favorites_on_label)))
            onView(withId(R.id.favorites_button)).perform(click())
            onView(withId(R.id.favorites_button))
                .check(matches(withText(R.string.favorites_label)))
        }
    }

    @Test
    fun searchInput_acceptsQueryWithoutCrashingEmptyTranscript() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.transcript_button)).perform(click())
            onView(withId(R.id.search_input)).perform(replaceText("hola"))
            closeSoftKeyboard()
            onView(withId(R.id.search_input)).check(matches(withText("hola")))
            onView(withId(R.id.empty_state_text)).check(matches(isDisplayed()))
            onView(withText(R.string.close)).perform(click())
            onView(withId(R.id.clear_button)).perform(click())
            onView(withId(R.id.transcript_button)).perform(click())
            onView(withId(R.id.search_input)).check(matches(withText("")))
            onView(withId(R.id.empty_state_text))
                .check(matches(withText(R.string.empty_state)))
            onView(withText(R.string.close)).perform(click())
            onView(withId(R.id.status_text))
                .check(matches(withText(R.string.status_ready)))
        }
    }

    @Test
    fun utilityDialogs_openAndCanBeDismissed() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.transcript_button)).perform(click())
            onView(withId(R.id.confidence_info_button)).perform(click())
            onView(withText(R.string.confidence_tooltip_title)).check(matches(isDisplayed()))
            onView(withText(R.string.confidence_tooltip_cta)).perform(click())
            onView(withText(R.string.close)).perform(click())

            onView(withId(R.id.phrasebook_button)).perform(scrollTo(), click())
            onView(withText(R.string.phrasebook_title)).check(matches(isDisplayed()))
            onView(withText(R.string.close)).perform(click())

            onView(withId(R.id.model_manager_button)).perform(scrollTo(), click())
            onView(withText(R.string.model_manager_title)).check(matches(isDisplayed()))
            onView(withText(R.string.close)).perform(click())

            onView(withId(R.id.app_title)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun emptyConversation_exportAndHistoryStaySafe() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.export_button)).perform(scrollTo(), click())
            // Empty transcript must not open the export-options dialog.
            onView(withText(R.string.export_dialog_title)).check(doesNotExist())
            onView(withId(R.id.app_title)).check(matches(isDisplayed()))

            onView(withId(R.id.history_button)).perform(scrollTo(), click())
            onView(withText(R.string.export_history_title)).check(doesNotExist())
            onView(withId(R.id.app_title)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun primaryActionButtons_remainReachable() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.transcript_button))
                .check(matches(allOf(isDisplayed(), isEnabled())))
            onView(withId(R.id.toggle_listening_button))
                .perform(scrollTo())
                .check(matches(allOf(isDisplayed(), isEnabled())))
            onView(withId(R.id.clear_button))
                .perform(scrollTo())
                .check(matches(allOf(isDisplayed(), isEnabled())))
            onView(withId(R.id.swap_language_button))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
            onView(withId(R.id.start_language_spinner))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
            onView(withId(R.id.speaker_spinner))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
        }
    }

    private fun seedPreferences(onboardingShown: Boolean) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .putBoolean(PREF_ONBOARDING_SHOWN, onboardingShown)
            .commit()
    }

    companion object {
        private const val PREFS_NAME = "offline_translator_prefs"
        private const val PREF_ONBOARDING_SHOWN = "pref_onboarding_shown"
    }
}
