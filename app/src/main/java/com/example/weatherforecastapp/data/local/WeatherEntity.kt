package com.example.weatherforecastapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity đại diện cho Bảng "weather_cache" trong CSDL SQLite Room.
 * Lưu trữ toàn bộ thông tin thời tiết hiện tại và dữ liệu dự báo phục vụ chế độ Offline.
 */
@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey
    val id: String = "current_location", // Khóa chính định danh vị trí (mặc định: "current_location")
    val cityName: String,                // Tên thành phố/địa điểm
    val temp: Double,                    // Nhiệt độ hiện tại (°C)
    val tempMin: Double,                 // Nhiệt độ thấp nhất trong ngày (°C)
    val tempMax: Double,                 // Nhiệt độ cao nhất trong ngày (°C)
    val description: String,             // Mô tả trạng thái thời tiết (VD: "mây rải rác")
    val iconCode: String,                // Mã biểu tượng thời tiết từ OpenWeatherMap
    val sunrise: Long = 0L,              // Unix timestamp giờ mặt trời mọc
    val sunset: Long = 0L,               // Unix timestamp giờ mặt trời lặn
    val airQuality: Int? = null,         // Chỉ số chất lượng không khí AQI (mức 1-5)
    val uvIndex: Double? = null,         // Chỉ số tia cực tím UV
    val hourlyForecastJson: String = "", // Chuỗi JSON mã hóa danh sách thời tiết theo giờ
    val fullForecastJson: String = "",   // Chuỗi JSON mã hóa toàn bộ dữ liệu dự báo 5 ngày
    val updatedAt: Long = System.currentTimeMillis() // Mốc thời gian lưu CSDL (System.currentTimeMillis)
)
