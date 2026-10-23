package com.example.weatherforecastapp.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val name: String? = null,
    val sys: Sys? = null,
    val weather: List<Weather> = emptyList(),
    val main: Main
)

data class Sys(
    val sunrise: Long = 0L,
    val sunset: Long = 0L
)

data class Weather(
    val description: String = "",
    val icon: String = ""
)

data class Main(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double? = null,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val humidity: Int? = null
)
