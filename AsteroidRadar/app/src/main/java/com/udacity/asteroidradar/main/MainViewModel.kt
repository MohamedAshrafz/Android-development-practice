package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.repository.AsteroidSelection
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    private val database = AsteroidDatabase.getInstance(application).asteroidDao
    private val repository = AsteroidRepository(database)


    private val _asteroids = repository.asteroids
    val asteroids: MutableLiveData<LiveData<List<Asteroid>>>
        get() = _asteroids

//    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.getAllAsteroids()){
//        it.asDomainModel()
//    }

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
        viewModelScope.launch {
            repository.getAsteroidsFromPeriod(AsteroidSelection.WEEK)
        }
    }

    fun selectFilter(selection: AsteroidSelection) {
        viewModelScope.launch {
            repository.getAsteroidsFromPeriod(selection)
        }
    }

}