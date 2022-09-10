package com.udacity.asteroidradar.database


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.DateUtils


@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate >= :todayDate ORDER BY closeApproachDate")
    fun getWeekAsteroids(todayDate: String = DateUtils.getToday()): LiveData<List<DatabaseAsteroid>>

    @Query("DELETE FROM asteroids_table WHERE closeApproachDate < :todayDate")
    fun deletePreviousDays(todayDate: String = DateUtils.getToday())

//    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate in (:nextWeekDays)")
//    fun getNextWeekAsteroids(nextWeekDays: List<String> = getNextSevenDaysFormattedDates()): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate = :todayDate ORDER BY closeApproachDate")
    fun getTodayAsteroids(todayDate: String = DateUtils.getToday()): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM asteroids_table ORDER BY closeApproachDate")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}

@Database(entities = [DatabaseAsteroid::class], version = 1, exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao

    companion object {

        @Volatile
        private var INSTANCE: AsteroidDatabase? = null

        fun getInstance(context: Context): AsteroidDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidDatabase::class.java,
                        "asteroids_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance

//                 return INSTANCE // does not work for smart casting problem
//                                // notice we have to return non-null object
            }
        }
    }
}
