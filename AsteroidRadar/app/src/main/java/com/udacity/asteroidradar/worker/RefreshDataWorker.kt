package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.repository.AsteroidSelection
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, parameters: WorkerParameters) :
    CoroutineWorker(appContext, parameters) {

    companion object {
        const val WORK_NAME = "AsteroidRefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getInstance(applicationContext).asteroidDao
        val repository = AsteroidRepository(database)

        return try {
            repository.getAsteroidsFromPeriod(AsteroidSelection.WEEK)
            repository.deleteOldDaysFromDatabase()

            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}