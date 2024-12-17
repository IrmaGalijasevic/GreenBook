package unsa.etf.rma.rmaprojekat

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import junit.framework.Assert.assertTrue
import junit.framework.TestCase.assertEquals
import org.hamcrest.CoreMatchers.anything
import org.junit.Rule
import org.junit.Test


class ValidationTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(
        MainActivity::class.java
    )


    private fun popuniOstalo(polje: Int) {

        if (polje != 1) onView(withId(R.id.nazivET)).perform(
            scrollTo(), typeText("Ime biljke"), closeSoftKeyboard()
        )

        if (polje != 2) onView(withId(R.id.porodicaET)).perform(
            scrollTo(), typeText("Porodica"), closeSoftKeyboard()
        )

        if (polje != 3) onView(withId(R.id.medicinskoUpozorenjeET)).perform(
            scrollTo(), typeText("Upozorenje"), closeSoftKeyboard()
        )
        if (polje != 4) {
            onView(withId(R.id.jeloET)).perform(
                scrollTo(), typeText("Neko jelo"), closeSoftKeyboard()
            )
            onView(withId(R.id.dodajJeloBtn)).perform(click())
        }
        if (polje != 5) {
            onView(withId(R.id.medicinskaKoristLV)).perform(scrollTo())
            onData(anything()).inAdapterView(withId(R.id.medicinskaKoristLV)).atPosition(0)
                .perform(click())
        }

        if (polje != 6) {
            onView(withId(R.id.zemljisniTipLV)).perform(scrollTo())
            onData(anything()).inAdapterView(withId(R.id.zemljisniTipLV)).atPosition(1)
                .perform(click())
        }

        if (polje != 7) {
            onView(withId(R.id.klimatskiTipLV)).perform(scrollTo())
            onData(anything()).inAdapterView(withId(R.id.klimatskiTipLV)).atPosition(2)
                .perform(click())
        }

        if (polje != 8) {
            onView(withId(R.id.profilOkusaLV)).perform(scrollTo())
            onData(anything()).inAdapterView(withId(R.id.profilOkusaLV)).atPosition(3)
                .perform(click())
        }
    }


    @Test
    fun testPopunjavanjeNaziva() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        val testiranoPolje = onView(withId(R.id.nazivET))

        popuniOstalo(1)
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
        testiranoPolje.check(matches(hasErrorText("Dužina imena biljke smije biti između 3 i 19 znakova")))

        testiranoPolje.perform(scrollTo(), typeText("b"), closeSoftKeyboard())
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
        testiranoPolje.check(matches(hasErrorText("Dužina imena biljke smije biti između 3 i 19 znakova")))

        testiranoPolje.perform(
            scrollTo(), typeText("ime biljke koje je predugacko"), closeSoftKeyboard()
        )
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
        testiranoPolje.check(matches(hasErrorText("Dužina imena biljke smije biti između 3 i 19 znakova")))

        testiranoPolje.perform(
            scrollTo(), clearText(), typeText("Ime biljke"), closeSoftKeyboard()
        )
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(doesNotExist())
    }

    @Test
    fun testPopunjavanjePorodice() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        val testiranoPolje = onView(withId(R.id.porodicaET))

        popuniOstalo(2)
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
        testiranoPolje.check(matches(hasErrorText("Dužina imena porodice smije biti između 3 i 19 znakova")))


        testiranoPolje.perform(scrollTo(), typeText("p"), closeSoftKeyboard())
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
        testiranoPolje.check(matches(hasErrorText("Dužina imena porodice smije biti između 3 i 19 znakova")))

        testiranoPolje.perform(
            scrollTo(), typeText("porodica biljke koja je predugacka"), closeSoftKeyboard()
        )
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
        testiranoPolje.check(matches(hasErrorText("Dužina imena porodice smije biti između 3 i 19 znakova")))

        testiranoPolje.perform(
            scrollTo(), clearText(), typeText("Porodica"), closeSoftKeyboard()
        )
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(doesNotExist())
    }

    @Test
    fun testPopunjavanjeMedicinskogUpozorenja() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        val testiranoPolje = onView(withId(R.id.medicinskoUpozorenjeET))

        popuniOstalo(3)
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
        testiranoPolje.check(matches(hasErrorText("Dužina teksta medicinskog upozorenja smije biti između 3 i 19 znakova")))


        testiranoPolje.perform(scrollTo(), typeText("u"), closeSoftKeyboard())
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
        testiranoPolje.check(matches(hasErrorText("Dužina teksta medicinskog upozorenja smije biti između 3 i 19 znakova")))

        testiranoPolje.perform(
            scrollTo(), typeText("upozorenje koje je predugacko"), closeSoftKeyboard()
        )
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
        testiranoPolje.check(matches(hasErrorText("Dužina teksta medicinskog upozorenja smije biti između 3 i 19 znakova")))

        testiranoPolje.perform(
            scrollTo(), clearText(), typeText("Upozorenje"), closeSoftKeyboard()
        )
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(doesNotExist())
    }

    @Test
    fun testPopunjavanjeJela(){


        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        popuniOstalo(4)
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.dodajBiljkuBtn))
            .check { view, _ ->
                val actualError = (view as Button).error
                assertEquals(actualError, "Niste odabrali polje u svim listama")
            }
        onView(withId(R.id.jeloET)).perform(
            scrollTo(), typeText("Neko jelo"), closeSoftKeyboard()
        )
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(doesNotExist())
    }
    @Test
    fun testPopunjavanjeMedicinskeKoristi() {
        val testiranoPolje = onView(withId(R.id.medicinskaKoristLV))

        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        popuniOstalo(5)
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))

        onView(ViewMatchers.withId(R.id.dodajBiljkuBtn))
            .check { view, _ ->
                val actualError = (view as Button).error
                assertEquals(actualError, "Niste odabrali polje u svim listama")
            }
        testiranoPolje.perform(scrollTo())
        onData(anything()).inAdapterView(withId(R.id.medicinskaKoristLV)).atPosition(0)
            .perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(doesNotExist())
    }



    @Test
    fun testPopunjavanjeZemljisnihTipova() {
        val testiranoPolje = onView(withId(R.id.zemljisniTipLV))

        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        popuniOstalo(6)
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))

        onView(withId(R.id.dodajBiljkuBtn))
            .check { view, _ ->
                val actualError = (view as Button).error
                assertEquals(actualError, "Niste odabrali polje u svim listama")
            }
        testiranoPolje.perform(scrollTo())
        onData(anything()).inAdapterView(withId(R.id.zemljisniTipLV)).atPosition(0)
            .perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(doesNotExist())
    }

    @Test
    fun testPopunjavanjeKlimatskihTipova() {
        val testiranoPolje = onView(withId(R.id.klimatskiTipLV))

        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        popuniOstalo(7)
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))

        onView(withId(R.id.dodajBiljkuBtn))
            .check { view, _ ->
                val actualError = (view as Button).error
                assertEquals(actualError, "Niste odabrali polje u svim listama")
            }
        testiranoPolje.perform(scrollTo())
        onData(anything()).inAdapterView(withId(R.id.klimatskiTipLV)).atPosition(0)
            .perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(doesNotExist())
    }

    @Test
    fun testPopunjavanjeProfilOkusa() {
        val testiranoPolje = onView(withId(R.id.profilOkusaLV))

        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        popuniOstalo(8)
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))

        onView(withId(R.id.dodajBiljkuBtn))
            .check { view, _ ->
                val actualError = (view as Button).error
                assertEquals(actualError, "Niste odabrali polje u svim listama")
            }
        testiranoPolje.perform(scrollTo())
        onData(anything()).inAdapterView(withId(R.id.profilOkusaLV)).atPosition(0)
            .perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo(), click())
        onView(withId(R.id.scrollView)).check(doesNotExist())
    }

    @Test
    fun testKamera(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())

        // Initialize Intents
        Intents.init()

        var initialBitmap: Bitmap? = null
        var finalBitmap: Bitmap? = null

        // Capture the initial bitmap before triggering the camera intent
        onView(withId(R.id.slikaIV)).check { view, _ ->
            val imageView = view as ImageView
            val drawable = imageView.drawable
            assertTrue(drawable is BitmapDrawable)
            initialBitmap = (drawable as BitmapDrawable).bitmap
        }

        // Prepare a bitmap to simulate the image capture result
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val resultIntent = Intent().apply {
            putExtra("data", bitmap)
        }

        // Respond to the camera intent with the simulated image capture result
        Intents.intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
            .respondWith(Instrumentation.ActivityResult(RESULT_OK, resultIntent))

        // Perform click on the button to trigger camera intent
        onView(withId(R.id.uslikajBiljkuBtn)).perform(click())

        //To click on the button if a dialog box shows up to grant permission to the app to use the camera
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val allowButton = device.findObject(UiSelector().text("Dok se aplikacija koristi").clickable(true))
        val allowButtonEn=device.findObject(UiSelector().text("Allow only while using the app").clickable(true))
        if (allowButton.exists()) {
            allowButton.click()
        }
        else if (allowButtonEn.exists()) {
            allowButtonEn.click()
        }
        // Capture the drawable of the ImageView after taking the picture
        onView(withId(R.id.slikaIV)).check { view, _ ->
            val imageView = view as ImageView
            val drawable = imageView.drawable
            assertTrue(drawable is BitmapDrawable)
            finalBitmap = (drawable as BitmapDrawable).bitmap
        }

        // Verify that the drawable before and after taking the picture is different
        assertTrue(initialBitmap != finalBitmap)

        // Release Intents
        Intents.release()
    }





}
