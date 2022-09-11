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
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    enum class LoadingStatusEnum {
        LOADING, COMPLETE
    }

    private val database = AsteroidDatabase.getInstance(application).asteroidDao
    private val repository = AsteroidRepository(database)


    private val _asteroids = repository.asteroids
    val asteroids: MutableLiveData<LiveData<List<Asteroid>>>
        get() = _asteroids

    private val _pictureOfTheDay = repository.pictureOfTheDay
    val pictureOfTheDay: LiveData<PictureOfDay>
        get() = _pictureOfTheDay

    private val _loadingStatus = MutableLiveData<LoadingStatusEnum>()
    val loadingStatus: LiveData<LoadingStatusEnum>
        get() = _loadingStatus

    private val _selectedAsteroid = MutableLiveData<Asteroid>(null)
    val selectedAsteroid: LiveData<Asteroid>
        get() = _selectedAsteroid

    fun onSelectAsteroid(asteroid: Asteroid) {
        _selectedAsteroid.value = asteroid
    }

    fun onClearSelected() {
        _selectedAsteroid.value = null
    }


    init {
        _loadingStatus.value = LoadingStatusEnum.LOADING
        viewModelScope.launch {
            repository.getAsteroidsFromPeriod(AsteroidSelection.WEEK)
            repository.refreshAsteroids()
            _loadingStatus.value = LoadingStatusEnum.COMPLETE
            repository.getPictureOfTheDay()
        }
    }

    fun selectFilter(selection: AsteroidSelection) {
        _loadingStatus.value = LoadingStatusEnum.LOADING
        viewModelScope.launch {
            repository.getAsteroidsFromPeriod(selection)
            _loadingStatus.value = LoadingStatusEnum.COMPLETE
        }
    }

}