/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.launch

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var tonight = MutableLiveData<SleepNight?>()

    private val nights = database.getAllNight()

    val nightString = Transformations.map(nights) { localNights ->
        formatNights(localNights, application.resources)
    }

    private val _eventNavigateToSleepQuality = MutableLiveData<SleepNight>()
    val eventNavigateToSleepQuality: LiveData<SleepNight>
        get() = _eventNavigateToSleepQuality

    fun clearEventNavigate() {
        _eventNavigateToSleepQuality.value = null
    }

    // setting the visibility of each button depending on the tonight and nights LiveData
    val startButtonVisible = Transformations.map(tonight) {
        return@map if (tonight.value == null)
            View.VISIBLE
        else
            View.INVISIBLE
    }

    val stopButtonVisible = Transformations.map(tonight) {
        return@map if (tonight.value != null)
            View.VISIBLE
        else
            View.INVISIBLE
    }

    val clearButtonVisible = Transformations.map(nights) {
        return@map if (nights.value?.isNotEmpty() == true)
            View.VISIBLE
        else
            View.INVISIBLE
    }

    private val _eventShowSnackbar = MutableLiveData<Boolean>(false)
    val eventShowSnackbar: LiveData<Boolean>
        get() = _eventShowSnackbar

    fun clearEventShowSnackbar() {
        _eventShowSnackbar.value = false
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        var night = database.getTonight()

        // if the night is completely set return null
        // (that's mean you need a new night to work with)
        if (night?.endTimeMilli != night?.startTimeMilli) {
            night = null
        }

        return night
    }

    fun onStartTracking() {
        viewModelScope.launch {
            startTracking()
        }
    }

    private suspend fun startTracking() {
        val night = SleepNight()
        insert(night)
        // update tonight
        tonight.value = getTonightFromDatabase()
    }

    private suspend fun insert(night: SleepNight) {
        database.insert(night)
    }

    fun onStopTracking() {
        viewModelScope.launch {
            // if there is no night started that's mean we cannot finish any night
            // return from launch and terminate the function call
            val oldNight = tonight.value ?: return@launch

            oldNight.endTimeMilli = System.currentTimeMillis()

            update(oldNight)

            _eventNavigateToSleepQuality.value = oldNight
        }
    }

    private suspend fun update(night: SleepNight) {
        database.update(night)
    }

    fun onClear() {
        viewModelScope.launch {
            clear()
        }
    }

    private suspend fun clear() {
        database.clear()

        tonight.value = null
        _eventShowSnackbar.value = true
    }
}
