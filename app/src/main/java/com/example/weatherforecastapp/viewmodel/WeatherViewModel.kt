package com.example.weatherforecastapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.WeatherApplication
import com.example.weatherforecastapp.data.local.WeatherEntity
import com.example.weatherforecastapp.model.ForecastItem
import com.example.weatherforecastapp.model.HourlyWeather
import com.example.weatherforecastapp.model.Main
import com.example.weatherforecastapp.model.Sys
import com.example.weatherforecastapp.model.Weather
import com.example.weatherforecastapp.model.WeatherResponse
import com.example.weatherforecastapp.repository.WeatherRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class WeatherUiState đóng gói toàn bộ trạng thái dữ liệu hiển thị trên giao diện (UI State).
 */
data class WeatherUiState(
    val currentWeather: WeatherResponse? = null,
    val hourlyForecast: List<HourlyWeather> = emptyList(),
    val fullForecastList: List<ForecastItem> = emptyList(),
    val airQuality: Int? = null,
    val uvIndex: Double? = null,
    val todayMaxTemp: Double? = null,
    val todayMinTemp: Double? = null,
    val isLoading: Boolean = false,
    val isOfflineData: Boolean = false,
    val error: String? = null
)

/**
 * WeatherViewModel quản lý logic nghiệp vụ và trạng thái UI cho các Fragment.
 * Kế thừa AndroidViewModel để truy cập Application Context và khởi tạo Room DB.
 */
class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    // Khởi tạo Database và Repository
    private val db = (application as? WeatherApplication)?.database
    private val repository = WeatherRepository(db?.weatherDao())
    private val gson = Gson()

    // Luồng dữ liệu trạng thái UIState dạng MutableStateFlow
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState

    init {
        // Tải dữ liệu cache từ CSDL Room ngay khi ViewModel được tạo
        loadCachedWeather()
    }

    /**
     * Đọc dữ liệu cục bộ từ Room CSDL hiển thị lập tức lên UI (Cơ chế Offline-First)
     */
    private fun loadCachedWeather() {
        viewModelScope.launch {
            try {
                val cached = repository.getCachedWeather()
                if (cached != null) {
                    Log.d("WeatherAPI", "Đã tìm thấy dữ liệu cache trong Room DB")
                    applyEntityToUiState(cached, isOffline = true)
                }
            } catch (e: Exception) {
                Log.e("WeatherAPI", "Lỗi đọc dữ liệu cache từ Room: ${e.message}")
            }
        }
    }

    /**
     * Gọi các REST API thời tiết trực tuyến theo tọa độ GPS (Lat, Lon)
     */
    fun loadWeather(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                Log.d("WeatherAPI", "Bắt đầu gọi API thời tiết - Tọa độ GPS: Lat=$lat, Lon=$lon")

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                // 1. Gọi Current Weather API (OpenWeatherMap)
                val current = repository.getCurrentWeather(lat, lon, apiKey)
                Log.d("WeatherAPI", "Current Weather thành công: ${current.main.temp}")

                // 2. Gọi Forecast API (OpenWeatherMap)
                val forecast = repository.getHourlyForecast(lat, lon, apiKey)
                Log.d("WeatherAPI", "Forecast thành công: số lượng ${forecast.list.size}")

                // 3. Gọi Air Quality API (AQI từ OpenWeatherMap)
                var aqiValue: Int? = null
                try {
                    val airQualityResponse = repository.getAirQuality(lat, lon, apiKey)
                    aqiValue = airQualityResponse.list.firstOrNull()?.main?.aqi
                    Log.d("WeatherAPI", "Air Quality thành công: AQI = $aqiValue")
                } catch (e: Exception) {
                    Log.e("WeatherAPI", "Gọi Air Quality thất bại: ${e.message}")
                }

                // 4. Gọi UV Index API (từ Open-Meteo API)
                var uvValue: Double? = null
                try {
                    uvValue = repository.getUvIndex(lat, lon)
                    Log.d("WeatherAPI", "UV Index thành công: UV = $uvValue")
                } catch (e: Exception) {
                    Log.e("WeatherAPI", "Gọi UV Index thất bại: ${e.message}")
                }

                // 5. Tính toán Nhiệt độ Max & Min chính xác trong ngày
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

                // Định dạng danh sách thời tiết theo giờ cho RecyclerView
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val hourlyList = forecast.list.take(6).map { item ->
                    val date = Date(item.dt * 1000L)
                    val time = sdf.format(date)
                    val iconCode = item.weather.firstOrNull()?.icon ?: "01d"
                    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                    val temp = item.main.temp.toInt()

                    HourlyWeather(
                        time,
                        "$temp°C",
                        iconUrl
                    )
                }

                // Cập nhật State lên giao diện UI
                _uiState.value = WeatherUiState(
                    currentWeather = current,
                    hourlyForecast = hourlyList,
                    fullForecastList = forecast.list,
                    airQuality = aqiValue,
                    uvIndex = uvValue,
                    todayMaxTemp = calculatedMaxTemp,
                    todayMinTemp = calculatedMinTemp,
                    isLoading = false,
                    isOfflineData = false
                )

                // 6. Lưu/ghi đè dữ liệu thời tiết mới thu thập vào Room CSDL SQLite
                val entity = WeatherEntity(
                    id = "current_location",
                    cityName = current.name ?: "Unknown",
                    temp = current.main.temp,
                    tempMin = calculatedMinTemp,
                    tempMax = calculatedMaxTemp,
                    description = current.weather.firstOrNull()?.description ?: "",
                    iconCode = current.weather.firstOrNull()?.icon ?: "01d",
                    sunrise = current.sys?.sunrise ?: 0L,
                    sunset = current.sys?.sunset ?: 0L,
                    airQuality = aqiValue,
                    uvIndex = uvValue,
                    hourlyForecastJson = gson.toJson(hourlyList),
                    fullForecastJson = gson.toJson(forecast.list),
                    updatedAt = System.currentTimeMillis()
                )
                repository.saveWeatherToCache(entity)
                Log.d("WeatherAPI", "Đã lưu dữ liệu mới vào Room DB thành công")

            } catch (e: Exception) {
                Log.e("WeatherAPI", "Gọi API thất bại: ${e.message}", e)

                // Nếu ngắt mạng, tự động khôi phục dữ liệu từ Room CSDL hiển thị trên UI
                val currentCache = repository.getCachedWeather()
                if (currentCache != null) {
                    applyEntityToUiState(currentCache, isOffline = true)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Không có kết nối mạng. Đang hiển thị dữ liệu offline."
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Lỗi khi tải dữ liệu thời tiết"
                    )
                }
            }
        }
    }

    /**
     * Chuyển đổi dữ liệu từ WeatherEntity (Room CSDL) sang WeatherUiState để cập nhật giao diện
     */
    private fun applyEntityToUiState(entity: WeatherEntity, isOffline: Boolean) {
        val weatherResponse = WeatherResponse(
            name = entity.cityName,
            sys = Sys(sunrise = entity.sunrise, sunset = entity.sunset),
            weather = listOf(Weather(description = entity.description, icon = entity.iconCode)),
            main = Main(
                temp = entity.temp,
                tempMin = entity.tempMin,
                tempMax = entity.tempMax
            )
        )

        val hourlyListType = object : TypeToken<List<HourlyWeather>>() {}.type
        val hourlyList: List<HourlyWeather> = try {
            gson.fromJson(entity.hourlyForecastJson, hourlyListType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        val fullForecastType = object : TypeToken<List<ForecastItem>>() {}.type
        val fullList: List<ForecastItem> = try {
            gson.fromJson(entity.fullForecastJson, fullForecastType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        _uiState.value = WeatherUiState(
            currentWeather = weatherResponse,
            hourlyForecast = hourlyList,
            fullForecastList = fullList,
            airQuality = entity.airQuality,
            uvIndex = entity.uvIndex,
            todayMaxTemp = entity.tempMax,
            todayMinTemp = entity.tempMin,
            isLoading = false,
            isOfflineData = isOffline
        )
    }
}
