package com.example.weatherforecastapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherforecastapp.databinding.FragmentA2Binding
import com.example.weatherforecastapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

class A2Fragment : Fragment() {

    private var _binding: FragmentA2Binding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by activityViewModels()

    private val apiKey = "7da87b1af74bf26f43862eca5b171b1b"
    private lateinit var hourlyAdapter: HourlyWeatherAdapter

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (hasLocationPermission()) {
                loadWeatherFromLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission denied. Showing default location.",
                    Toast.LENGTH_SHORT
                ).show()
                loadDefaultLocation()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentA2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeWeatherState()
        setupClickListeners()

        if (hasLocationPermission()) {
            loadWeatherFromLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setupRecyclerView() {
        hourlyAdapter = HourlyWeatherAdapter()
        binding.hourlyRecyclerView.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupClickListeners() {
        binding.weatherForecastCard.setOnClickListener {
            findNavController().navigate(R.id.action_a2Fragment_to_a3Fragment)
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

    private fun loadWeatherFromLocation() {
        if (!hasLocationPermission()) {
            loadDefaultLocation()
            return
        }

        try {
            val locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val providers = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER
            )

            val location: Location? = providers
                .filter { provider -> locationManager.isProviderEnabled(provider) }
                .mapNotNull { provider ->
                    try {
                        locationManager.getLastKnownLocation(provider)
                    } catch (_: SecurityException) {
                        null
                    }
                }
                .maxByOrNull { it.time }

            if (location != null) {
                viewModel.loadWeather(location.latitude, location.longitude, apiKey)
            } else {
                loadDefaultLocation()
            }

        } catch (_: SecurityException) {
            loadDefaultLocation()
        }
    }

    private fun loadDefaultLocation() {
        // Vị trí mặc định (Ví dụ: Hà Nội)
        viewModel.loadWeather(21.0285, 105.8542, apiKey)
    }

    private fun observeWeatherState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    // Hiển thị thông báo lỗi nếu có
                    state.error?.let { errorMessage ->
                        if (errorMessage.isNotBlank()) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Cập nhật dữ liệu thời tiết hiện tại lên UI màn A2
                    state.currentWeather?.let { weather ->
                        binding.currentTemp.text = getString(R.string.temp_format, weather.main.temp)
                        binding.description.text = weather.weather.firstOrNull()?.description ?: "--"
                        binding.maxMin.text = getString(
                            R.string.max_min_format,
                            weather.main.tempMax,
                            weather.main.tempMin
                        )

                        // Nếu bạn muốn hiển thị cả tên thành phố ở A2, có thể gán vào đây:
                        // binding.tvCityName?.text = weather.name

                        val iconCode = weather.weather.firstOrNull()?.icon
                        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                        Glide.with(this@A2Fragment).load(iconUrl).into(binding.mainWeatherIcon)
                    }

                    // Cập nhật danh sách dự báo theo giờ cho khung Today
                    hourlyAdapter.updateData(state.hourlyForecast)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}