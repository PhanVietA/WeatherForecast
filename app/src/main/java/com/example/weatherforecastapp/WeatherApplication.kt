package com.example.weatherforecastapp

import android.app.Application
import com.example.weatherforecastapp.data.local.AppDatabase

/**
 * Lớp WeatherApplication đại diện cho cấp Ứng dụng (Application level).
 * Khởi tạo CSDL Room Database duy nhất (Singleton) dùng chung cho toàn bộ ứng dụng.
 */
class WeatherApplication : Application() {

    // Khởi tạo lười (lazy) CSDL Room Database khi ứng dụng vừa khởi chạy
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
