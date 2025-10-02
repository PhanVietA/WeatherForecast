package com.example.weatherforecastapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecastapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

class A2Fragment : Fragment() {

    private val viewModel: WeatherViewModel by this.viewModels()
    private val apiKey = "7da87b1af74bf26f43862eca5b171b1b"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_a2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val window = requireActivity().window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())

        val tempText = view.findViewById<TextView>(R.id.currentTemp)
        val descText = view.findViewById<TextView>(R.id.description)
        val maxMinText = view.findViewById<TextView>(R.id.maxMin)
        val iconView = view.findViewById<ImageView>(R.id.mainWeatherIcon)

        val recyclerView = view.findViewById<RecyclerView>(R.id.hourlyRecyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = HourlyWeatherAdapter(
            listOf(
                HourlyWeather("15:00", "19°", R.mipmap.ic_weather1),
                HourlyWeather("16:00", "18°", R.mipmap.ic_weather1),
                HourlyWeather("17:00", "20°", R.mipmap.ic_weather1),
                HourlyWeather("18:00", "21°", R.mipmap.ic_weather1),
                HourlyWeather("19:00", "20°", R.mipmap.ic_weather1),
                HourlyWeather("20:00", "21°", R.mipmap.ic_weather1)
            )
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadWeather(21.0285, 105.8542, apiKey)
        }

        // Quan sát dữ liệu
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weather.collect { data ->
                data?.let {
                    tempText.text = "${it.main.temp}°C"
                    descText.text = it.weather.firstOrNull()?.description ?: "--"
                    maxMinText.text = "Max: ${it.main.tempMax}°C / Min: ${it.main.tempMin}°C"

                    val iconCode = it.weather.firstOrNull()?.icon
                    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                    Glide.with(this@A2Fragment).load(iconUrl).into(iconView)
                }
            }
        }

        // Navigation
        val btnMenu = view.findViewById<ImageButton>(R.id.btnMenu)
        val btnLocation = view.findViewById<ImageButton>(R.id.btnLocation)
        val btnAdd = view.findViewById<ImageButton>(R.id.btnAdd)

        btnMenu.setOnClickListener {
            findNavController().navigate(R.id.action_a2Fragment_to_splashFragment)
        }
        btnLocation.setOnClickListener {
            findNavController().navigate(R.id.action_a2Fragment_to_a3Fragment)
        }
        btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_a2Fragment_to_a3Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val window = requireActivity().window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.show(WindowInsetsCompat.Type.statusBars())
    }
}
