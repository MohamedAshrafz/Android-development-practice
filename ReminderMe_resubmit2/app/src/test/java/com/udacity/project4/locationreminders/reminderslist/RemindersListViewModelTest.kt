package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import com.udacity.project4.R

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // using the fake repository as a test double repository for viewModel
    private lateinit var remindersRepository: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel

    @Before
    fun setupViewModelWithRepository() {
        stopKoin()
        remindersRepository = FakeDataSource()

        // using androidX libraries to get the app context
        // and setting the fake repository as a test double repository for viewModel using dependency injection
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            remindersRepository
        )
    }

    @After
    fun cleanRepository() = mainCoroutineRule.runBlockingTest {
        remindersRepository.deleteAllReminders()
    }

    // check_loading TEST
    // testing the check_loading (showLoading LiveData)
    // the loading screen must be shown right after the calling of loadReminders()
    // and before the coroutine starts, so we can pause the coroutine
    // till we test the loading is correctly displayed
    @Test
    fun loadRemindersList_loading() {

        // some dummy data added to the repository
        remindersRepository.addReminders(
            ReminderDTO(
                "reminder1",
                "this is the first test reminder",
                "location1",
                10.7,
                10.9
            ),
            ReminderDTO(
                "reminder2",
                "this is the first test reminder",
                "location2",
                20.7,
                20.9
            ),
            ReminderDTO(
                "reminder3",
                "this is the first test reminder",
                "location3",
                30.7,
                30.9
            ),
            ReminderDTO(
                "reminder4",
                "this is the first test reminder",
                "location4",
                40.7,
                40.9
            )
        )

        // pause the mainDispatcher so we can verify the initial value of loading
        mainCoroutineRule.pauseDispatcher()

        // loading the data from the database
        remindersListViewModel.loadReminders()

        // asserting the showLoading value as still loading(true)
        // we use getOrAwaitValue to make it easy to set observer to the liveData
        // without a lot of pilar plate code to set the observer manually
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        // resuming the coroutine
        mainCoroutineRule.resumeDispatcher()

        // asserting the showLoading value as completed loading(false)
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))

    }

    // shouldReturnError TEST (required in resubmission)
    // testing with shouldReturnError
    // setting a flag in the fake repository which force the it to return error to asserting that
    @Test
    fun loadStatisticsWhenTasksAreUnavailable_returnError() {

        // some dummy data added to the repository
        remindersRepository.addReminders(
            ReminderDTO(
                "reminder15",
                "this is the s test reminder",
                "location15",
                15.7,
                15.9
            ),
            ReminderDTO(
                "reminder16",
                "this is the s test reminder",
                "location16",
                25.7,
                25.9
            )
        )

        // WHEN - setting the shouldReturnError making the repository return errors
        // even though there exist data in it
        remindersRepository.setShouldReturnError(true)
        remindersListViewModel.loadReminders()

        // THEN - asserting that the showSnackBar is properly showing the error massage with
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(), `is`("Test Error - getReminders"))
    }

    // showNoData TEST
    // given no items in the repository
    // the show no data icon is correctly displayed
    @Test
    fun loadStatisticsWhenTasksAreUnavailable_showNoData() {

        // Given no data in the repository

        // WHEN - setting the shouldReturnError making the repository return errors
        // even though there exist data in it
        remindersListViewModel.loadReminders()

        // THEN - asserting that showNoData is true
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))
    }

}