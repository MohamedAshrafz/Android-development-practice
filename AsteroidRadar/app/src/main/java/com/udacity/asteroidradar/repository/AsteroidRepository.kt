package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.DatabaseAsteroid
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

class AsteroidRepository(private val database: AsteroidDao) {

    val pictureOfTheDay = MutableLiveData<PictureOfDay>()
    var asteroids = MutableLiveData<LiveData<List<Asteroid>>>()

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
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

    suspend fun getAsteroidsFromPeriod(selection: AsteroidSelection) {

        asteroids.value = withContext(Dispatchers.IO) {

            val databaseAsteroidList: LiveData<List<DatabaseAsteroid>> =
                when (selection) {
                    AsteroidSelection.ALL -> database.getAllAsteroids()

                    AsteroidSelection.TODAY -> database.getTodayAsteroids()

                    else -> database.getWeekAsteroids()
                }
            return@withContext Transformations.map(databaseAsteroidList) { it.asDomainModel() }
        }
    }

    suspend fun getPictureOfTheDay() {
        pictureOfTheDay.value = withContext(Dispatchers.IO) {

            try {
                return@withContext Network.NetworkService.getImageOfTheDay()
            } catch (e: Exception) {
                Timber.i("no internet connection ${e.message}")
                return@withContext null
            }
        }
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