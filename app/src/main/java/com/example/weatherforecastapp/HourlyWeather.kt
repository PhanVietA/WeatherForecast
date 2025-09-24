package com.example.weatherforecast.model

data class HourlyWeather(
    val hour: String,
    val temperature: String,
    val iconRes: Int
)
