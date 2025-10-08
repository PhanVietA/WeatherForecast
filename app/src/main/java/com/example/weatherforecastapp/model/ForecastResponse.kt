package com.example.weatherforecastapp.model

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt: Long,
    val main: MainForecast,
    val weather: List<WeatherForecast>
)

data class MainForecast(
    val temp: Double
)

data class WeatherForecast(
    val icon: String
)
