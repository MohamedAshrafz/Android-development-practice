package com.udacity.project4

import android.app.Activity
import android.app.Application
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.CustomMatchers
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    // for the data binding to work correctly with espresso
    val dataBindingIdlingResource = DataBindingIdlingResource()

    // register the idling resources for synchronization
    // it basically tells espresso if the app is idling or busy
    @Before
    fun registerIdlingResources() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    // unregister the idling resources
    @Before
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }


    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Test
    fun addReminder_endToEnd() {
        // Start up Tasks screen
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // making the dataBindingIdlingResource monitor the current activity
        dataBindingIdlingResource.monitorActivity(activityScenario)


        // clicking on the add fab to go to add reminder screen
        onView(withId(R.id.addReminderFAB)).perform(click())

        // making a new reminder as just an helper for setting
        val newReminder = ReminderDTO(
            "title1",
            "desc1",
            "location1",
            40.7,
            30.5
        )

        // setting the reminder details
        onView(withId(R.id.reminderTitle)).perform(replaceText(newReminder.title))
        onView(withId(R.id.reminderDescription)).perform(replaceText(newReminder.description))
        onView(withId(R.id.reminderLocation)).perform(replaceText(newReminder.location))


        // clicking on the save fab for saving the new reminder
        onView(withId(R.id.saveReminder)).perform(click())

        // checking that the snackbar is displayed with the added correctly text
        onView(ViewMatchers.withText(appContext.getString(R.string.reminder_saved)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


        // navigating back with up button
        onView(withContentDescription(activityScenario.getToolbarNavigationContentDescription()))
            .perform(click())


        // THEN checking if there exist a recycler view with an item in position 0
        onView(withId(R.id.reminderssRecyclerView))
            // checking the item is displayed
            .check(
                ViewAssertions.matches(
                    CustomMatchers.positionWithTitle(
                        0,
                        ViewMatchers.isDisplayed()
                    )
                )
            )

        val position = 0

        // THEN checking if there exist a recycler view with an item in position 0
        onView(withId(R.id.reminderssRecyclerView))
            // checking the item is displayed
            .check(
                ViewAssertions.matches(
                    CustomMatchers.positionWithTitle(
                        position,
                        ViewMatchers.isDisplayed()
                    )
                )
            )
            // checking if the item has the right title
            .check(
                ViewAssertions.matches(
                    CustomMatchers.positionWithTitle(
                        position,
                        ViewMatchers.hasDescendant(ViewMatchers.withText(newReminder.title))
                    )
                )
            )
            // checking if the item has the right description
            .check(
                ViewAssertions.matches(
                    CustomMatchers.positionWithTitle(
                        position,
                        ViewMatchers.hasDescendant(ViewMatchers.withText(newReminder.description))
                    )
                )
            )

        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }

    @Test
    fun navigation_endToEnd() {
        // Start up Tasks screen
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // making the dataBindingIdlingResource monitor the current activity
        dataBindingIdlingResource.monitorActivity(activityScenario)


        // clicking on the add fab to go to add reminder screen
        onView(withId(R.id.addReminderFAB)).perform(click())

        // clicking on the save fab for saving the new reminder
        onView(withId(R.id.saveReminder)).perform(click())

        // checking that the snackbar is displayed with the added correctly text
        onView(ViewMatchers.withText(appContext.getString(R.string.error_no_data)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // navigating back with up button
        onView(withContentDescription(activityScenario.getToolbarNavigationContentDescription()))
            .perform(click())

        // clicking on the add reminder fab for saving the new reminder
        onView(withId(R.id.addReminderFAB)).perform(click())

        // pressing the system back button
        pressBack()

        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }

}

fun <T : Activity> ActivityScenario<T>.getToolbarNavigationContentDescription()
        : String {
    var description = ""
    onActivity {
        description =
            it.findViewById<Toolbar>(R.id.action_bar).navigationContentDescription as String
    }
    return description
}
