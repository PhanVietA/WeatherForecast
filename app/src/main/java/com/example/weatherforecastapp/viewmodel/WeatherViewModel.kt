package com.example.weatherforecastapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.HourlyWeather
import com.example.weatherforecastapp.model.WeatherResponse
import com.example.weatherforecastapp.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    private val _hourlyWeather = MutableStateFlow<List<HourlyWeather>>(emptyList())
    val hourlyWeather: StateFlow<List<HourlyWeather>> = _hourlyWeather

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

    fun loadHourlyForecast(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val result = repository.getHourlyForecast(lat, lon, apiKey)
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

                val hourlyList = result.list.take(6).map { item ->
                    val date = Date(item.dt * 1000L)
                    val time = sdf.format(date)
                    val iconCode = item.weather.firstOrNull()?.icon ?: "01d"
                    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                    val temp = item.main.temp.toInt()

                    HourlyWeather(
                        hour = time,
                        temperature = "$temp°C",
                        iconUrl = iconUrl
                    )
                }

                _hourlyWeather.value = hourlyList

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
