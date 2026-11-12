package com.example.weatherforecastapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecastapp.databinding.FragmentWeatherDetailBinding
import com.example.weatherforecastapp.model.DailyForecast
import com.example.weatherforecastapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class WeatherDetailFragment : Fragment(R.layout.fragment_weather_detail) {

    private var _binding: FragmentWeatherDetailBinding? = null
    private val binding get() = _binding!!

    // Sử dụng activityViewModels để dùng chung dữ liệu từ WeatherViewModel với HomeFragment
    private val viewModel: WeatherViewModel by activityViewModels()

    // Khai báo ForecastAdapter cho RecyclerView ngang ở màn WeatherDetail
    private lateinit var forecastAdapter: ForecastAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWeatherDetailBinding.bind(view)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Khởi tạo Adapter
        forecastAdapter = ForecastAdapter()
        binding.rvForecast.apply {
            adapter = forecastAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // 1. Lắng nghe dữ liệu thời tiết hiện tại (Tên địa điểm, Max/Min Temp, Bình minh/Hoàng hôn)
                    state.currentWeather?.let { weather ->
                        val cityName = weather.name ?: "Unknown"
                        val maxTemp = (state.todayMaxTemp ?: weather.main.tempMax).roundToInt()
                        val minTemp = (state.todayMinTemp ?: weather.main.tempMin).roundToInt()

                        // Cập nhật TextView Location & Temp theo strings.xml
                        binding.tvLocationAndTemp.text = getString(R.string.location_temp_format, cityName, maxTemp, minTemp)

                        weather.sys?.let { sys ->
                            // Định dạng giờ Bình minh & Hoàng hôn từ Unix timestamp
                            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                            val sunriseStr = timeFormat.format(Date(sys.sunrise * 1000L))
                            val sunsetStr = timeFormat.format(Date(sys.sunset * 1000L))

                            binding.tvSunriseTime.text = getString(R.string.sunrise_prefix, sunriseStr)
                            binding.tvSunsetTime.text = getString(R.string.sunset_prefix, sunsetStr)
                        }
                    }

                    // 2. Gom nhóm dữ liệu dự báo theo Ngày (thay vì hiển thị theo mốc 3 giờ)
                    if (state.fullForecastList.isNotEmpty()) {
                        val dateKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dayDisplayFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
                        val todayKey = dateKeyFormat.format(Date())

                        val dailyList = state.fullForecastList
                            .groupBy { item ->
                                dateKeyFormat.format(Date(item.dt * 1000L))
                            }
                            .map { (dateKey, itemsForDay) ->
                                // Chọn mốc thời điểm khoảng 12:00 trưa làm đại diện nhiệt độ trong ngày
                                val representativeItem = itemsForDay.minByOrNull { item ->
                                    val hour = SimpleDateFormat("HH", Locale.getDefault()).format(Date(item.dt * 1000L)).toInt()
                                    kotlin.math.abs(hour - 12)
                                } ?: itemsForDay.first()

                                val dayLabel = if (dateKey == todayKey) {
                                    "Today"
                                } else {
                                    dayDisplayFormat.format(Date(representativeItem.dt * 1000L))
                                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                }

                                val iconCode = representativeItem.weather.firstOrNull()?.icon ?: "01d"
                                val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"

                                DailyForecast(
                                    time = dayLabel,
                                    temperature = "${representativeItem.main.temp.toInt()}°",
                                    iconResId = R.drawable.ic_crosshairs,
                                    iconUrl = iconUrl
                                )
                            }

                        forecastAdapter.updateData(dailyList)
                    }

                    // 3. Lắng nghe Chất lượng không khí (AQI) từ API Air Pollution
                    state.airQuality?.let { aqi ->
                        val aqiStringId = when (aqi) {
                            1 -> R.string.aqi_level_1
                            2 -> R.string.aqi_level_2
                            3 -> R.string.aqi_level_3
                            4 -> R.string.aqi_level_4
                            else -> R.string.aqi_level_5
                        }
                        binding.tvAirQualityDetails.text = getString(aqiStringId)
                    }

                    // 4. Cập nhật chỉ số UV thực tế từ API
                    state.uvIndex?.let { uv ->
                        binding.tvUvValue.text = if (uv % 1.0 == 0.0) {
                            uv.toInt().toString()
                        } else {
                            String.format(Locale.getDefault(), "%.1f", uv)
                        }

                        val uvLevelResId = when {
                            uv < 3.0 -> R.string.uv_low
                            uv < 6.0 -> R.string.uv_moderate
                            uv < 8.0 -> R.string.uv_high
                            uv < 11.0 -> R.string.uv_very_high
                            else -> R.string.uv_extreme
                        }
                        binding.tvUvLevel.text = getString(uvLevelResId)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
