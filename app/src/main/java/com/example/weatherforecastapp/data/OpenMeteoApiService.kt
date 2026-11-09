package com.example.weatherforecastapp.data

import com.example.weatherforecastapp.model.OpenMeteoResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface OpenMeteoApiService định nghĩa các câu lệnh HTTP GET tương tác với Open-Meteo REST API.
 */
interface OpenMeteoApiService {

    // Lấy chỉ số tia cực tím UV Index theo tọa độ GPS thời gian thực
    @GET("v1/forecast")
    suspend fun getUvIndex(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "uv_index"
    ): OpenMeteoResponse
}
