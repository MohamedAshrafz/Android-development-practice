package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.repository.AsteroidSelection
import com.udacity.asteroidradar.repository.LoadingStatus
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    private val database = AsteroidDatabase.getInstance(application).asteroidDao
    private val repository = AsteroidRepository(database)

    private val _asteroids = repository.asteroids
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pictureOfTheDay: LiveData<PictureOfDay?> = repository.pictureOfTheDay
    val pictureOfTheDay: LiveData<PictureOfDay?>
        get() = _pictureOfTheDay

    val loadingStatus: LiveData<LoadingStatus> = repository.loadingStatus

    private val _selectedAsteroid = MutableLiveData<Asteroid?>()
    val selectedAsteroid: LiveData<Asteroid?>
        get() = _selectedAsteroid

    fun onSelectAsteroid(asteroid: Asteroid) {
        _selectedAsteroid.value = asteroid
    }

    fun onClearSelected() {
        _selectedAsteroid.value = null
    }


    init {
        viewModelScope.launch {
            repository.setSelector(AsteroidSelection.WEEK)
            repository.refreshAsteroids()
        }
    }

    fun selectFilter(selection: AsteroidSelection) {
        viewModelScope.launch {
            repository.setSelector(selection)
        }
    }

}