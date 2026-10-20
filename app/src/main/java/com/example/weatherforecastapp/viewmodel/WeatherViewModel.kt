package com.example.weatherforecastapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.model.HourlyWeather
import com.example.weatherforecastapp.model.WeatherResponse
import com.example.weatherforecastapp.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WeatherUiState(
    val currentWeather: WeatherResponse? = null,
    val hourlyForecast: List<HourlyWeather> = emptyList(),
    val fullForecastList: List<Any> = emptyList(),
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
                Log.d("WeatherAPI", "Bắt đầu gọi API thời tiết")
                Log.d("WeatherAPI", "Latitude: $lat")
                Log.d("WeatherAPI", "Longitude: $lon")

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                Log.d("WeatherAPI", "Đang gọi Current Weather API")

                val current = repository.getCurrentWeather(
                    lat,
                    lon,
                    apiKey
                )

                Log.d("WeatherAPI", "Current Weather API thành công")
                Log.d("WeatherAPI", "Nhiệt độ hiện tại: ${current.main.temp}")

                Log.d("WeatherAPI", "Đang gọi Forecast API")

                val forecast = repository.getHourlyForecast(
                    lat,
                    lon,
                    apiKey
                )

                Log.d("WeatherAPI", "Forecast API thành công")
                Log.d("WeatherAPI", "Số lượng dữ liệu forecast: ${forecast.list.size}")

                val sdf = SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                )

                val hourlyList = forecast.list.take(6).map { item ->
                    val date = Date(item.dt * 1000L)
                    val time = sdf.format(date)

                    val iconCode = item.weather
                        .firstOrNull()
                        ?.icon
                        ?: "01d"

                    val iconUrl =
                        "https://openweathermap.org/img/wn/${iconCode}@2x.png"

                    val temp = item.main.temp.toInt()

                    HourlyWeather(
                        time,
                        "$temp°C",
                        iconUrl
                    )
                }

                _uiState.value = WeatherUiState(
                    currentWeather = current,
                    hourlyForecast = hourlyList,
                    fullForecastList = forecast.list,
                    isLoading = false
                )

                Log.d("WeatherAPI", "Đã cập nhật dữ liệu lên UI")

            } catch (e: Exception) {

                Log.e(
                    "WeatherAPI",
                    "Gọi API thất bại: ${e.message}",
                    e
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Lỗi khi tải dữ liệu thời tiết"
                )
            }
        }
    }
}
