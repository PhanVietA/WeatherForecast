package com.example.weatherforecastapp.data

import com.example.weatherforecastapp.model.OpenMeteoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApiService {
    @GET("v1/forecast")
    suspend fun getUvIndex(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "uv_index"
    ): OpenMeteoResponse
}
