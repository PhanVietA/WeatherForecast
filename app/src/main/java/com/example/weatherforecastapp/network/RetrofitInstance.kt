package com.example.weatherforecastapp.network

import com.example.weatherforecastapp.data.WeatherApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton RetrofitInstance khởi tạo Retrofit Client kết nối tới OpenWeatherMap API.
 */
object RetrofitInstance {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    // Khởi tạo OkHttpClient với Bộ ghi nhật ký HttpLoggingInterceptor ghi lại request/response
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    // Khởi tạo đối tượng WeatherApiService
    val api: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Tự động ánh xạ JSON -> Kotlin Object
            .build()
            .create(WeatherApiService::class.java)
    }
}
