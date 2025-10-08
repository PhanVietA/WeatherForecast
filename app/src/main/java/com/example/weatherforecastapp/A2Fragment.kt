package com.example.weatherforecastapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.weatherforecastapp.databinding.FragmentA2Binding
import com.example.weatherforecastapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

class A2Fragment : Fragment() {

    private var _binding: FragmentA2Binding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModels()
    private val apiKey = "7da87b1af74bf26f43862eca5b171b1b"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentA2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HourlyWeatherAdapter(emptyList())
        binding.hourlyRecyclerView.adapter = adapter

        viewModel.loadWeather(21.0285, 105.8542, apiKey)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->

                state.error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }

                state.currentWeather?.let { weather ->
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

                adapter.updateData(state.hourlyForecast)
            }
        }

        binding.btnMenu.setOnClickListener {
            findNavController().navigate(R.id.action_a2Fragment_to_splashFragment)
        }

        binding.btnLocation.setOnClickListener {
            findNavController().navigate(R.id.action_a2Fragment_to_a3Fragment)
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_a2Fragment_to_a3Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
