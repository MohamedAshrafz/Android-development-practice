package com.udacity.project4.locationreminders.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource(ReminderLocalRepository)
class FakeAndroidDataSource : ReminderDataSource {

    private val targetContext: Context = ApplicationProvider.getApplicationContext<Context>()

    // reminders map to save every key with single reminder
    private val remindersMap: LinkedHashMap<String, ReminderDTO> = linkedMapOf()

    private var shouldReturnError: Boolean = false

    // setting a flag to return a an error from getReminder and getReminders
    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (shouldReturnError)
            Result.Error(targetContext.getString(R.string.testError_getReminders))
        else
            Result.Success(remindersMap.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersMap[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if (shouldReturnError) {
            Result.Error(targetContext.getString(R.string.testError_getReminder))
        } else {
            val result = remindersMap[id]
            if (result == null)
                Result.Error(targetContext.getString(R.string.reminder_not_found))
            else
                Result.Success(remindersMap[id] as ReminderDTO)
        }
    }

    override suspend fun deleteAllReminders() {
        remindersMap.clear()
    }

    fun addReminders(vararg tasks: ReminderDTO) {
        for (task in tasks) {
            remindersMap[task.id] = task // this is equal to tasksServiceData.set(task.id, task)
        }
    }
}