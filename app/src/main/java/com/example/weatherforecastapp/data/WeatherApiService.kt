package com.example.weatherforecastapp.data

import com.example.weatherforecastapp.model.WeatherResponse
import com.example.weatherforecastapp.model.ForecastResponse
import com.example.weatherforecastapp.model.AirQualityResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface WeatherApiService định nghĩa các câu lệnh HTTP GET tương tác với OpenWeatherMap REST API.
 */
interface WeatherApiService {

    // Lấy thông tin thời tiết hiện tại theo tọa độ GPS
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric", // Đơn vị nhiệt độ Celsius
        @Query("lang") lang: String = "vi"
    ): WeatherResponse

    // Lấy thông tin danh sách dự báo thời tiết 5 ngày / 3 giờ một lần
    @GET("forecast")
    suspend fun getHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "vi"
    ): ForecastResponse

    // Lấy chỉ số chất lượng không khí ô nhiễm AQI
    @GET("air_pollution")
    suspend fun getAirQuality(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): AirQualityResponse
}
