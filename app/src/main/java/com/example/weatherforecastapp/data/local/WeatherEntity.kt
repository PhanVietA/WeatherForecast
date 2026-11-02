package com.example.weatherforecastapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey
    val id: String = "current_location",
    val cityName: String,
    val temp: Double,
    val tempMin: Double,
    val tempMax: Double,
    val description: String,
    val iconCode: String,
    val sunrise: Long = 0L,
    val sunset: Long = 0L,
    val airQuality: Int? = null,
    val uvIndex: Double? = null,
    val hourlyForecastJson: String = "",
    val fullForecastJson: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
