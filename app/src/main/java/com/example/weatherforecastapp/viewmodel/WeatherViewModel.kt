package com.example.weatherforecastapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.model.WeatherResponse
import com.example.weatherforecastapp.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    fun loadWeather(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val result = repository.getCurrentWeather(lat, lon, apiKey)
                _weather.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
