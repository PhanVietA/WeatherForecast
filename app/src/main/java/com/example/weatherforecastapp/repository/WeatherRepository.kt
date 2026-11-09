package com.example.weatherforecastapp.repository

import com.example.weatherforecastapp.data.local.WeatherDao
import com.example.weatherforecastapp.data.local.WeatherEntity
import com.example.weatherforecastapp.model.AirQualityResponse
import com.example.weatherforecastapp.model.ForecastResponse
import com.example.weatherforecastapp.network.OpenMeteoClient
import com.example.weatherforecastapp.network.RetrofitInstance

/**
 * Lớp WeatherRepository chịu trách nhiệm điều phối luồng dữ liệu (Data Layer).
 * Tích hợp nguồn API trực tuyến (OpenWeatherMap & Open-Meteo) và nguồn dữ liệu cục bộ Room CSDL.
 */
class WeatherRepository(
    private val weatherDao: WeatherDao? = null
) {

    // Gọi API OpenWeatherMap lấy thông tin thời tiết hiện tại
    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String) =
        RetrofitInstance.api.getCurrentWeather(lat, lon, apiKey)

    // Gọi API OpenWeatherMap lấy danh sách dự báo thời tiết 5 ngày / 3 giờ
    suspend fun getHourlyForecast(lat: Double, lon: Double, apiKey: String): ForecastResponse {
        return RetrofitInstance.api.getHourlyForecast(lat, lon, apiKey)
    }

    // Gọi API OpenWeatherMap lấy chỉ số chất lượng không khí AQI
    suspend fun getAirQuality(lat: Double, lon: Double, apiKey: String): AirQualityResponse {
        return RetrofitInstance.api.getAirQuality(lat, lon, apiKey)
    }

    // Gọi API Open-Meteo lấy chỉ số tia cực tím UV Index thời gian thực
    suspend fun getUvIndex(lat: Double, lon: Double): Double {
        return try {
            OpenMeteoClient.api.getUvIndex(lat, lon).current?.uvIndex ?: 0.0
        } catch (_: Exception) {
            0.0 // Mặc định trả về 0.0 nếu gặp lỗi mạng
        }
    }

    // Đọc dữ liệu cache thời tiết từ CSDL Room SQLite
    suspend fun getCachedWeather(id: String = "current_location"): WeatherEntity? {
        return weatherDao?.getWeatherById(id)
    }

    // Lưu/ghi đè dữ liệu thời tiết mới nhất vào CSDL Room SQLite
    suspend fun saveWeatherToCache(entity: WeatherEntity) {
        weatherDao?.insertWeather(entity)
    }
}
