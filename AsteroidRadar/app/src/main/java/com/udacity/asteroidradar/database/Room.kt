package com.udacity.asteroidradar.database


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Constants
import java.text.SimpleDateFormat
import java.util.*



@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate > :todayDate")
    fun getWeekAsteroids(todayDate: String): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate = :todayDate")
    fun getTodayAsteroids(todayDate: String): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM asteroids_table")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>
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

fun main() {
    getToday()
}

fun getToday(): String {

    val currentTime = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

    // returns todayFormatted
    println(dateFormat.format(currentTime))
    return dateFormat.format(currentTime)
}
