package com.example.weatherforecastapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.model.HourlyWeather
import com.example.weatherforecastapp.model.WeatherResponse
import com.example.weatherforecastapp.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class WeatherUiState(
    val currentWeather: WeatherResponse? = null,
    val hourlyForecast: List<HourlyWeather> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun loadWeather(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val current = repository.getCurrentWeather(lat, lon, apiKey)
                val forecast = repository.getHourlyForecast(lat, lon, apiKey)
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

                val hourlyList = forecast.list.take(6).map { item ->
                    val date = Date(item.dt * 1000L)
                    val time = sdf.format(date)
                    val iconCode = item.weather.firstOrNull()?.icon ?: "01d"
                    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                    val temp = item.main.temp.toInt()
                    HourlyWeather(time, "$temp°C", iconUrl)
                }

                _uiState.value = WeatherUiState(
                    currentWeather = current,
                    hourlyForecast = hourlyList,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Lỗi khi tải dữ liệu thời tiết"
                )
            }
        }
    }
}
