package com.udacity.asteroidradar.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.ApiKey
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.DateUtils
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


object Network {
    val NetworkService: AsteroidApiInterface by lazy {
        retrofit.create(AsteroidApiInterface::class.java)
    }
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()

interface AsteroidApiInterface {

    @GET("neo/rest/v1/feed")
    suspend fun getWeekAsteroids(
        @Query("start_date") startDate: String = DateUtils.getToday(),
        @Query("end_date") endDate: String = DateUtils.getEndOfCurrentWeekDay(),
        @Query("api_key") apiKey: String = ApiKey.API_KEY
    ): String

    @GET("planetary/apod")
    suspend fun getImageOfTheDay(
        @Query("api_key") apiKey: String = ApiKey.API_KEY
    ): PictureOfDay
}

