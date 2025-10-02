package com.example.weatherforecastapp.repository

import com.example.weatherforecastapp.model.WeatherResponse
import com.example.weatherforecastapp.network.RetrofitClient

class WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String): WeatherResponse {
        return RetrofitClient.apiService.getCurrentWeather(lat, lon, apiKey)
    }
}
