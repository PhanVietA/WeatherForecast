package com.example.weatherforecastapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecastapp.adapter.ForecastAdapter
import com.example.weatherforecastapp.databinding.FragmentA3Binding
import com.example.weatherforecastapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class A3Fragment : Fragment() {

    private var _binding: FragmentA3Binding? = null
    private val binding get() = _binding!!

    // Dùng activityViewModels() thay cho viewModels() để chia sẻ dữ liệu với A2
    private val viewModel: WeatherViewModel by activityViewModels()

    private lateinit var forecastAdapter: ForecastAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentA3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeWeather()
    }

    private fun setupRecyclerView() {
        forecastAdapter = ForecastAdapter()
        binding.rvForecast.apply {
            adapter = forecastAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeWeather() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    // 1. Hiển thị thông tin thời tiết hiện tại & Header của A3
                    state.currentWeather?.let { weather ->
                        // Tên địa điểm
                        binding.tvLocationA3.text = weather.name ?: "North America"

                        // Nhiệt độ Max / Min trong ngày
                        binding.tvTempRangeA3.text = getString(
                            R.string.max_min_format,
                            weather.main.tempMax,
                            weather.main.tempMin
                        )

                        // Bình minh & Hoàng hôn (Chuyển đổi từ Unix timestamp sang giờ)
                        binding.tvSunriseTime.text = "Sunrise: ${formatTimestamp(weather.sys.sunrise)}"
                        binding.tvSunsetTime.text = "Sunset: ${formatTimestamp(weather.sys.sunset)}"
                    }

                    // 2. Cập nhật danh sách dự báo (7 ngày / theo giờ) vào RecyclerView
                    if (state.hourlyForecast.isNotEmpty()) {
                        forecastAdapter.updateData(state.hourlyForecast)
                    }

                    // 3. Xử lý hiển thị lỗi nếu có
                    state.error?.let { errorMessage ->
                        if (errorMessage.isNotBlank()) {
                            Toast.makeText(
                                requireContext(),
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    // Hàm phụ trợ đổi timestamp sang định dạng giờ "h:mm a"
    private fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) return "--:--"
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000L))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
