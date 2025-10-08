package com.example.weatherforecastapp.repository

import com.example.weatherforecastapp.model.ForecastResponse
import com.example.weatherforecastapp.network.RetrofitInstance

class WeatherRepository {

    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String) =
        RetrofitInstance.api.getCurrentWeather(lat, lon, apiKey)

    suspend fun getHourlyForecast(lat: Double, lon: Double, apiKey: String): ForecastResponse {
        return RetrofitInstance.api.getHourlyForecast(lat, lon, apiKey)
    }
}
