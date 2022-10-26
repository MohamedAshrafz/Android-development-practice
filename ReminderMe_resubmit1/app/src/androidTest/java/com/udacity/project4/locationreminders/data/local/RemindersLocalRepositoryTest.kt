package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // setup the reminder local repository
    // using in memory database builder because it's for testing
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        remindersLocalRepository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }

    // closing the database after each test completes
    @After
    fun closeDB() = database.close()

    // testing inserting a reminder to the repository then retrieving it to make sure the data
    // is saved correctly and permanently
    @Test
    fun saveReminder_retrieveSameReminder() = runBlocking {
        // making a new reminder to be saved in the database
        val reminder = ReminderDTO(
            "reminder1",
            "this is the first test reminder",
            "location1",
            10.7,
            10.9
        )

        // GIVEN - a new task is saved to the repository
        remindersLocalRepository.saveReminder(reminder)

        // WHEN - retrieving the same reminder from the repository
        val loadedReminder = remindersLocalRepository.getReminder(reminder.id)

        // THEN - same task should be returned
        assertThat(loadedReminder as Result.Success, not(nullValue()))
        assertThat(loadedReminder.data.id, `is`(reminder.id))
        assertThat(loadedReminder.data.title, `is`(reminder.title))
        assertThat(loadedReminder.data.description, `is`(reminder.description))
        assertThat(loadedReminder.data.location, `is`(reminder.location))
        assertThat(loadedReminder.data.latitude, `is`(reminder.latitude))
        assertThat(loadedReminder.data.longitude, `is`(reminder.longitude))
    }

    // Predictable errors like data not found test (required in resubmission)
    @Test
    fun getReminder_dataNotFoundError() = runBlocking {
        // making a new reminder
        val reminder = ReminderDTO(
            "reminder4",
            "this is the first test reminder",
            "location4",
            40.7,
            40.9
        )

        // GIVEN - not adding the reminder to the database

        // WHEN - testing retrieving the reminder from the database
        val loadedReminder = remindersLocalRepository.getReminder(reminder.id)

        // THEN - getting the result error with "Reminder not found!"
        assertThat(loadedReminder as Result.Error, not(nullValue()))
        assertThat(loadedReminder.message, `is`("Reminder not found!"))
    }
}