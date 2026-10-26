package com.example.weatherforecastapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class A2Fragment : Fragment() {

    private var _binding: FragmentA2Binding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by activityViewModels()

    private val apiKey = "2863621e04b2f2761eab71d2a45ec4f0"
    private lateinit var hourlyAdapter: HourlyWeatherAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                loadWeatherFromLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Quyền vị trí bị từ chối. Hiển thị vị trí mặc định.",
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupRecyclerView()
        observeWeatherState()
        setupClickListeners()

        checkPermissionAndLoadLocation()
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
        binding.btnLocation.setOnClickListener {
            Toast.makeText(requireContext(), "Đang cập nhật vị trí...", Toast.LENGTH_SHORT).show()
            checkPermissionAndLoadLocation()
        }
    }

    private fun checkPermissionAndLoadLocation() {
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

    @SuppressLint("MissingPermission")
    private fun loadWeatherFromLocation() {
        if (!hasLocationPermission()) {
            loadDefaultLocation()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d("WeatherLocation", "Lấy lastLocation thành công: Lat=${location.latitude}, Lon=${location.longitude}")
                    viewModel.loadWeather(location.latitude, location.longitude, apiKey)
                } else {
                    requestFreshLocation()
                }
            }
            .addOnFailureListener {
                requestFreshLocation()
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestFreshLocation() {
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.d("WeatherLocation", "Lấy currentLocation mới thành công: Lat=${location.latitude}, Lon=${location.longitude}")
                viewModel.loadWeather(location.latitude, location.longitude, apiKey)
            } else {
                fallbackToLocationManager()
            }
        }.addOnFailureListener {
            fallbackToLocationManager()
        }
    }

    @SuppressLint("MissingPermission")
    private fun fallbackToLocationManager() {
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
                Log.d("WeatherLocation", "Lấy LocationManager thành công: Lat=${location.latitude}, Lon=${location.longitude}")
                viewModel.loadWeather(location.latitude, location.longitude, apiKey)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Không thể xác định vị trí. Hiển thị vị trí mặc định (Hà Nội).",
                    Toast.LENGTH_SHORT
                ).show()
                loadDefaultLocation()
            }
        } catch (_: Exception) {
            loadDefaultLocation()
        }
    }

    private fun loadDefaultLocation() {
        // Vị trí mặc định (Hà Nội)
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

                    // Cập nhật ngày tháng thực tế cho phần Today (Ví dụ: "July, 25")
                    val currentDateStr = SimpleDateFormat("MMMM, d", Locale.ENGLISH).format(Date())
                    binding.dateText.text = currentDateStr

                    // Cập nhật dữ liệu thời tiết hiện tại lên UI màn A2
                    state.currentWeather?.let { weather ->
                        val cityName = weather.name ?: "--"
                        binding.tvCityName.text = cityName

                        binding.currentTemp.text = getString(R.string.temp_format, weather.main.temp)
                        binding.description.text = weather.weather.firstOrNull()?.description ?: "--"
                        binding.maxMin.text = getString(
                            R.string.max_min_format,
                            weather.main.tempMax,
                            weather.main.tempMin
                        )

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
