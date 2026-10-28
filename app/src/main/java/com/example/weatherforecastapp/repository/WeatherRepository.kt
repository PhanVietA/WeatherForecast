package com.example.weatherforecastapp.repository

import com.example.weatherforecastapp.model.ForecastResponse
import com.example.weatherforecastapp.model.AirQualityResponse
import com.example.weatherforecastapp.network.OpenMeteoClient
import com.example.weatherforecastapp.network.RetrofitInstance

class WeatherRepository {

    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String) =
        RetrofitInstance.api.getCurrentWeather(lat, lon, apiKey)

    suspend fun getHourlyForecast(lat: Double, lon: Double, apiKey: String): ForecastResponse {
        return RetrofitInstance.api.getHourlyForecast(lat, lon, apiKey)
    }

    suspend fun getAirQuality(lat: Double, lon: Double, apiKey: String): AirQualityResponse {
        return RetrofitInstance.api.getAirQuality(lat, lon, apiKey)
    }

    suspend fun getUvIndex(lat: Double, lon: Double): Double {
        return try {
            OpenMeteoClient.api.getUvIndex(lat, lon).current?.uvIndex ?: 0.0
        } catch (_: Exception) {
            0.0
        }
    }
}
