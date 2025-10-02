package com.example.weatherforecastapp.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main
)

data class Weather(
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double
)
