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

    private val viewModel: WeatherViewModel by viewModels()
    private val apiKey = "7da87b1af74bf26f43862eca5b171b1b"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_a2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ẩn thanh status bar
        val window = requireActivity().window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())

        // Liên kết view
        val tempText = view.findViewById<TextView>(R.id.currentTemp)
        val descText = view.findViewById<TextView>(R.id.description)
        val maxMinText = view.findViewById<TextView>(R.id.maxMin)
        val iconView = view.findViewById<ImageView>(R.id.mainWeatherIcon)

        val recyclerView = view.findViewById<RecyclerView>(R.id.hourlyRecyclerView)
        val adapter = HourlyWeatherAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadWeather(21.0285, 105.8542, apiKey)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadHourlyForecast(21.0285, 105.8542, apiKey)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weather.collect { data ->
                data?.let {
                    tempText.text = getString(R.string.temp_format, it.main.temp)
                    descText.text = it.weather.firstOrNull()?.description ?: "--"
                    maxMinText.text = getString(
                        R.string.max_min_format,
                        it.main.tempMax,
                        it.main.tempMin
                    )

                    val iconCode = it.weather.firstOrNull()?.icon
                    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                    Glide.with(this@A2Fragment).load(iconUrl).into(iconView)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hourlyWeather.collect { list ->
                adapter.updateData(list)
            }
        }

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
