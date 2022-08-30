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

package com.example.android.trackmysleepquality.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.launch

class SleepQualityViewModel(
    private val nightKey: Long = 0L,
    private val database: SleepDatabaseDao
) : ViewModel() {

    private val _eventNavigateToSleepTracker = MutableLiveData<Boolean>(false)
    val eventNavigateToSleepTracker: LiveData<Boolean>
        get() = _eventNavigateToSleepTracker

    fun clearEventNavigate() {
        _eventNavigateToSleepTracker.value = false
    }

    fun onSetSleepQualityTo(quality: Int) {
        viewModelScope.launch {
            val night = database.get(nightKey) ?: return@launch

            night.sleepQuality = quality
            database.update(night)

            _eventNavigateToSleepTracker.value = true
        }
    }
}
