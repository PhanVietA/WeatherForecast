package com.example.weatherforecastapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Interface WeatherDao (Data Access Object) định nghĩa các câu lệnh SQL thao tác với CSDL Room.
 */
@Dao
interface WeatherDao {

    // Chèn hoặc ghi đè dữ liệu thời tiết mới nhất vào bảng weather_cache
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    // Lấy dữ liệu cache thời tiết theo ID (mặc định "current_location")
    @Query("SELECT * FROM weather_cache WHERE id = :id")
    suspend fun getWeatherById(id: String = "current_location"): WeatherEntity?

    // Quan sát luồng dữ liệu thời tiết qua Flow (tự động phát tín hiệu khi CSDL thay đổi)
    @Query("SELECT * FROM weather_cache WHERE id = :id")
    fun getWeatherFlow(id: String = "current_location"): Flow<WeatherEntity?>

    // Xóa toàn bộ dữ liệu cache trong bảng weather_cache
    @Query("DELETE FROM weather_cache")
    suspend fun clearCache()
}
