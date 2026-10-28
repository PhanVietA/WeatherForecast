package com.example.weatherforecastapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.model.ForecastItem
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
    val fullForecastList: List<ForecastItem> = emptyList(),
    val airQuality: Int? = null, // Thêm trường lưu AQI cho màn A3
    val uvIndex: Double? = null, // Thêm trường lưu UV Index cho màn A3
    val todayMaxTemp: Double? = null, // Nhiệt độ cao nhất trong ngày
    val todayMinTemp: Double? = null, // Nhiệt độ thấp nhất trong ngày
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

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                // 1. Gọi Current Weather API
                val current = repository.getCurrentWeather(lat, lon, apiKey)
                Log.d("WeatherAPI", "Current Weather thành công: ${current.main.temp}")

                // 2. Gọi Forecast API
                val forecast = repository.getHourlyForecast(lat, lon, apiKey)
                Log.d("WeatherAPI", "Forecast thành công: số lượng ${forecast.list.size}")

                // 3. Gọi Air Quality API (Phục vụ card Air Quality ở màn A3)
                var aqiValue: Int? = null
                try {
                    val airQualityResponse = repository.getAirQuality(lat, lon, apiKey)
                    aqiValue = airQualityResponse.list.firstOrNull()?.main?.aqi
                    Log.d("WeatherAPI", "Air Quality thành công: AQI = $aqiValue")
                } catch (e: Exception) {
                    Log.e("WeatherAPI", "Gọi Air Quality thất bại: ${e.message}")
                }

                // 4. Gọi UV Index API từ Open-Meteo (Phục vụ card UV Index ở màn A3)
                var uvValue: Double? = null
                try {
                    uvValue = repository.getUvIndex(lat, lon)
                    Log.d("WeatherAPI", "UV Index thành công: UV = $uvValue")
                } catch (e: Exception) {
                    Log.e("WeatherAPI", "Gọi UV Index thất bại: ${e.message}")
                }

                // 5. Tính toán Nhiệt độ Max & Min chính xác trong ngày từ danh sách forecast
                val dateKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayKey = dateKeyFormat.format(Date())
                val todayItems = forecast.list.filter { item ->
                    dateKeyFormat.format(Date(item.dt * 1000L)) == todayKey
                }

                val allTodayTemps = (todayItems.map { it.main.temp } +
                        forecast.list.take(8).map { it.main.temp } +
                        listOf(current.main.temp, current.main.tempMax, current.main.tempMin))

                val calculatedMaxTemp = allTodayTemps.maxOrNull() ?: current.main.tempMax
                val calculatedMinTemp = allTodayTemps.minOrNull() ?: current.main.tempMin

                // Format danh sách thời tiết theo giờ cho RecyclerView
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val hourlyList = forecast.list.take(6).map { item ->
                    val date = Date(item.dt * 1000L)
                    val time = sdf.format(date)

                    val iconCode = item.weather
                        .firstOrNull()
                        ?.icon
                        ?: "01d"

                    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                    val temp = item.main.temp.toInt()

                    HourlyWeather(
                        time,
                        "$temp°C",
                        iconUrl
                    )
                }

                // Cập nhật toàn bộ State
                _uiState.value = WeatherUiState(
                    currentWeather = current,
                    hourlyForecast = hourlyList,
                    fullForecastList = forecast.list,
                    airQuality = aqiValue,
                    uvIndex = uvValue,
                    todayMaxTemp = calculatedMaxTemp,
                    todayMinTemp = calculatedMinTemp,
                    isLoading = false
                )

                Log.d("WeatherAPI", "Đã cập nhật dữ liệu lên UI thành công")

            } catch (e: Exception) {
                Log.e("WeatherAPI", "Gọi API thất bại: ${e.message}", e)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Lỗi khi tải dữ liệu thời tiết"
                )
            }
        }
    }
}
