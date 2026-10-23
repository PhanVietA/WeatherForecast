package com.example.weatherforecastapp.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    val list: List<ForecastItem> = emptyList()
)

data class ForecastItem(
    val dt: Long,
    val main: MainForecast,
    val weather: List<WeatherForecast> = emptyList(),
    @SerializedName("dt_txt") val dtTxt: String? = null
)

data class MainForecast(
    val temp: Double
)

data class WeatherForecast(
    val icon: String
)
