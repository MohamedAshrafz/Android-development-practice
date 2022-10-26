package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

// FakeDataSource acts as a test double to the LocalDataSource(ReminderLocalRepository)
class FakeDataSource : ReminderDataSource {

    // reminders map to save every key with single reminder
    private val remindersMap: LinkedHashMap<String, ReminderDTO> = linkedMapOf()

    private var shouldReturnError: Boolean = false

    // setting a flag to return a an error from getReminder and getReminders
    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return try {
            if (shouldReturnError)
                throw Exception("Reminders not found!")
            else
                Result.Success(remindersMap.values.toList())
        }catch (ex:Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersMap[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return try {
            if (shouldReturnError) {
                throw Exception("Test Error!")
            } else {
                val result = remindersMap[id]
                if (result == null)
                    throw Exception("Reminder not found!")
                else
                    Result.Success(remindersMap[id] as ReminderDTO)
            }
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
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