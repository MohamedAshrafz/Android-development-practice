package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeAndroidDataSource
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.CustomMatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {

    // add testing for the error messages.

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    // i will be using koin as service locator here too
    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = ApplicationProvider.getApplicationContext()
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
            single { FakeAndroidDataSource() as ReminderDataSource }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our Fake repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    // cleaning the repository from the testing data
    @After
    fun resetRepo() = runBlockingTest {
        repository.deleteAllReminders()
    }

    // testing the navigation of the fragment to the add new reminder fragment
    // then we verify that the right navigation happened correctly
    @Test
    fun clickFab_navigateToAddReminder() {

        // GAVEN - the testing scenario have a mocked navController
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)

        // setting the navController with the fragment
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - clicking on the fab button
        onView(withId(R.id.addReminderFAB))
            .perform(click())

        // THEN
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    // checking if the ui displays the data correctly
    // testing the navigation of the fragment to the add new reminder fragment
    // then we test if it is displayed correctly in the recycler view
    @Test
    fun addingRemindersToTheRecyclerView_recyclerViewShowsTheAddedItems() = runBlockingTest {
        // GIVEN saving this to tasks to view in the recycler view
        repository.saveReminder(
            ReminderDTO(
                "reminder1",
                "this is the first test reminder",
                "location1",
                10.7,
                10.9
            )
        )
        repository.saveReminder(
            ReminderDTO(
                "reminder2",
                "this is the second test reminder",
                "location2",
                20.7,
                20.9
            )
        )

        // WHEN reminder fragment launched with the given navController
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)

        // setting the navController with the fragment
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // THEN checking if there exist a recycler view with an item in position 0
        onView(withId(R.id.reminderssRecyclerView))
            // checking the item is displayed
            .check(matches(CustomMatchers.positionWithTitle(1, isDisplayed())))

        onView(withId(R.id.reminderssRecyclerView))
            // checking if the item has the right title
            .check(
                matches(
                    CustomMatchers.positionWithTitle(
                        0,
                        hasDescendant(withText("reminder1"))
                    )
                )
            )

        // THEN checking if there exist a recycler view with an item in position 1
        onView(withId(R.id.reminderssRecyclerView))
            // checking the item is displayed
            .check(matches(CustomMatchers.positionWithTitle(1, isDisplayed())))

        onView(withId(R.id.reminderssRecyclerView))
            // checking if the item has the right title
            .check(
                matches(
                    CustomMatchers.positionWithTitle(
                        1,
                        hasDescendant(withText("reminder2"))
                    )
                )
            )
    }
}