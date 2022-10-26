package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    // getting the executed rules for instrumented testing
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var database: RemindersDatabase

    // setting inMemoryDatabase for testing before each test case
    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    // closing the database after each test completes
    @After
    fun closeDB() = database.close()

    // testing inserting a reminder to the database then retrieving it to make sure the data
    // is saved correctly and permanently
    @Test
    fun insertReminderAndGetReminderById_returnReminder() = runBlockingTest {
        // setting up a dummy reminder
        val reminder =
            ReminderDTO(
                "reminder1",
                "this is the first test reminder",
                "location1",
                30.7,
                50.9
            )

        // GIVEN saving reminder to the database
        database.reminderDao().saveReminder(reminder)

        // THEN trying to get it using the reminder id
        val loadedReminder = database.reminderDao().getReminderById(reminder.id)

        assertThat(loadedReminder as ReminderDTO, not(nullValue()))
        assertThat(loadedReminder.title, `is`(reminder.title))
        assertThat(loadedReminder.description, `is`(reminder.description))
        assertThat(loadedReminder.location, `is`(reminder.location))
        assertThat(loadedReminder.latitude, `is`(reminder.latitude))
        assertThat(loadedReminder.longitude, `is`(reminder.longitude))
    }

    // the same last test but with a list of reminder and trying to retrieving them all
    // then checking if the same list of reminder is retrieved correctly
    @Test
    fun insertListOfRemindersAndAllReminders_returnListOfTheSameReminders() = runBlockingTest {
        // setting up a dummy reminders
        val reminders = listOf(
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


        // GIVEN saving reminders to the database
        for (reminder in reminders) {
            database.reminderDao().saveReminder(reminder)
        }

        // THEN trying to get all the reminders from the database
        // and asserting that they are the same reminders
        val loadedRemindersList = database.reminderDao().getReminders()

        for ((index, loadedReminder) in loadedRemindersList.withIndex()) {
            assertThat(loadedReminder as ReminderDTO, not(nullValue()))
            assertThat(loadedReminder.title, `is`(reminders[index].title))
            assertThat(loadedReminder.description, `is`(reminders[index].description))
            assertThat(loadedReminder.location, `is`(reminders[index].location))
            assertThat(loadedReminder.latitude, `is`(reminders[index].latitude))
            assertThat(loadedReminder.longitude, `is`(reminders[index].longitude))
        }
    }

    // testing getting data which does not exist in the database
    // and making sure it will return an null value
    @Test
    fun errorDataNotFound_returnNullValue() = runBlockingTest {
        // GIVEN - a reminder
        // setting up a dummy reminder
        val reminder =
            ReminderDTO(
                "reminder1",
                "this is the first test reminder",
                "location1",
                30.7,
                50.9
            )

        // WHEN trying to get reminder which was not added to the database
        val loadedReminder = database.reminderDao().getReminderById(reminder.id)

        // THEN asserting that it should return a null value
        assertThat(loadedReminder, nullValue())
    }

}

