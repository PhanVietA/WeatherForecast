package com.example.weatherforecastapp.model

data class DailyForecast(
    val time: String,
    val temperature: String,
    val iconResId: Int,
    val iconUrl: String? = null
)
