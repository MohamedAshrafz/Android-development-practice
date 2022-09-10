package com.udacity.asteroidradar

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getToday(): String {

        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

        // returns todayFormatted
        println(dateFormat.format(currentTime))
        return dateFormat.format(currentTime)
    }

    fun getEndOfCurrentWeekDay(): String {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)

        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

        // returns 7 day after today formatted
        println(dateFormat.format(currentTime))
        return dateFormat.format(currentTime)
    }

    fun getNextSevenDaysFormattedDates(): ArrayList<String> {
        val formattedDateList = ArrayList<String>()

        val calendar = Calendar.getInstance()
        for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
            val currentTime = calendar.time
            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
            formattedDateList.add(dateFormat.format(currentTime))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return formattedDateList
    }
}