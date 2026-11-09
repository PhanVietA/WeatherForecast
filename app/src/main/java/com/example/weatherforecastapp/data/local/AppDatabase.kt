package com.example.weatherforecastapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Lớp AppDatabase kế thừa RoomDatabase, quản lý CSDL SQLite "weather_database".
 * Áp dụng mẫu thiết kế Singleton đảm bảo chỉ tạo duy nhất 1 instance CSDL trong toàn bộ ứng dụng.
 */
@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Cung cấp giao diện truy vấn WeatherDao
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Khởi tạo hoặc lấy ra Instance CSDL an toàn đa luồng (Thread-safe)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
