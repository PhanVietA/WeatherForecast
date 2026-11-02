package com.example.weatherforecastapp.network

import com.example.weatherforecastapp.data.OpenMeteoApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenMeteoClient {
    private const val BASE_URL = "https://api.open-meteo.com/"

    val api: OpenMeteoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApiService::class.java)
    }
}
