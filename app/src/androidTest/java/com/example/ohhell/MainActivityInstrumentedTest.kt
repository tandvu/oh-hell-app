package com.example.ohhell

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {

    @Test
    fun addPlayer_doesNotCrash() {
        // Launch the activity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Type a name and click Add
            onView(ViewMatchers.withId(R.id.etPlayerName))
                .perform(ViewActions.typeText("EspressoUser"), ViewActions.closeSoftKeyboard())

            onView(ViewMatchers.withId(R.id.btnAddPlayer))
                .perform(ViewActions.click())

            // If the app crashes during click, the test will fail. No explicit assertion needed here.
        }
    }
}
