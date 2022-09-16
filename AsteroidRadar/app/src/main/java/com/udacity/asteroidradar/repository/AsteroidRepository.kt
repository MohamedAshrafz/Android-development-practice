package com.udacity.asteroidradar.repository

import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

enum class AsteroidSelection {
    WEEK, TODAY, ALL
}

enum class LoadingStatus {
    LOADING, COMPLETE
}


class AsteroidRepository(private val database: AsteroidDao) {

    private val selector = MutableLiveData<AsteroidSelection>()

    fun setSelector(selection: AsteroidSelection) {
        selector.value = selection
    }


    val asteroids: LiveData<List<Asteroid>> = selector.switchMap { selector ->
        liveData(context = Dispatchers.IO) {
            _loadingStatus.postValue(LoadingStatus.LOADING)
            emitSource(getAsteroidsFromPeriod(selector))
            _loadingStatus.postValue(LoadingStatus.COMPLETE)
        }
    }

    private val _loadingStatus = MutableLiveData<LoadingStatus>(LoadingStatus.LOADING)
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    val pictureOfTheDay: LiveData<PictureOfDay?> = liveData(context = Dispatchers.IO) {
        try {
            emit(Network.NetworkService.getImageOfTheDay())
        } catch (e: Exception) {
            Timber.i("no internet connection ${e.message}")
            emit(null)
        }
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            _loadingStatus.postValue(LoadingStatus.LOADING)
            try {
                // getting the asteroids from the internet
                val asteroidsList =
                    fromRawStringToDomainList(Network.NetworkService.getWeekAsteroids())

                val dbArray = asteroidsList.asDatabaseModel()
                database.insertAll(*dbArray)
            } catch (e: Exception) {
                Timber.i("no internet connection ${e.message}")
            }
        }
    }

    private fun getAsteroidsFromPeriod(selection: AsteroidSelection): LiveData<List<Asteroid>> {

        return when (selection) {
            AsteroidSelection.ALL -> database.getAllAsteroids()

            AsteroidSelection.TODAY -> database.getTodayAsteroids()

            else -> database.getWeekAsteroids()
        }.map { it.asDomainModel() }
    }

    suspend fun deleteOldDaysFromDatabase() {
        withContext(Dispatchers.IO) {
            database.deletePreviousDays()
        }
    }


    private fun fromRawStringToDomainList(jsonString: String): List<Asteroid> {
        val jsonObject = JSONObject(jsonString)
        return parseAsteroidsJsonResult(jsonObject)
    }
}