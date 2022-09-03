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
 *
 */

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.launch

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */

enum class MarsApiStatus { LOADING, ERROR, DONE }
class OverviewViewModel : ViewModel() {

//    // The internal MutableLiveData String that stores the status of the most recent request
//    private val _response = MutableLiveData<String>()
//
//    // The external immutable LiveData for the request status String
//    val response: LiveData<String>
//        get() = _response

    // For determining the status of server loading
    private val _status = MutableLiveData<MarsApiStatus>()

    val status: LiveData<MarsApiStatus>
        get() = _status

    // one of the items in the response list
    private val _properties = MutableLiveData<List<MarsProperty>>()
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    private val _selectedProperty = MutableLiveData<MarsProperty>()
    val selectedProperty: LiveData<MarsProperty>
        get() = _selectedProperty

    fun setNavigateToDetailScreen(marsProperty: MarsProperty) {
        _selectedProperty.value = marsProperty
    }

    fun clearNavigateToDetailScreen() {
        _selectedProperty.value = null
    }

    fun updateItemsInList(filter: MarsApiFilter) {
        getMarsRealEstateProperties(filter)
    }

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        viewModelScope.launch {
            _status.value = MarsApiStatus.LOADING
            try {
                val propertiesList = MarsApi.retrofitService.getPropertiesAsync(filter.value)
                _status.value = MarsApiStatus.DONE
                if (propertiesList.isNotEmpty()) {
                    _properties.value = propertiesList
                }
            } catch (e: Exception) {
                _status.value = MarsApiStatus.ERROR
            }
        }
    }
}