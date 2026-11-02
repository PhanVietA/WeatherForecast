package com.example.weatherforecastapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("SELECT * FROM weather_cache WHERE id = :id")
    suspend fun getWeatherById(id: String = "current_location"): WeatherEntity?

    @Query("SELECT * FROM weather_cache WHERE id = :id")
    fun getWeatherFlow(id: String = "current_location"): Flow<WeatherEntity?>

    @Query("DELETE FROM weather_cache")
    suspend fun clearCache()
}
