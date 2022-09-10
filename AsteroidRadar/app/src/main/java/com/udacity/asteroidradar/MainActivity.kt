package com.udacity.asteroidradar


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())


//        coroutineScope.launch {
//            val asteroidsString: String = Network.NetworkService.getWeekAsteroids()
//
//            val jsonObject = JSONObject(asteroidsString)
//            val asteroidsInWeek = parseAsteroidsJsonResult(jsonObject)
//            println(asteroidsInWeek)
//        }

    }
}
