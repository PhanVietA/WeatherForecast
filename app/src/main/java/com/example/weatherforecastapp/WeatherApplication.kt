package com.example.weatherforecastapp

import android.app.Application
import com.example.weatherforecastapp.data.local.AppDatabase

class WeatherApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
