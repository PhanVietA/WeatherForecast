package com.example.weatherforecastapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class A2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_a2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ẩn status bar bằng WindowInsetsControllerCompat
        val window = requireActivity().window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val recyclerView = view.findViewById<RecyclerView>(R.id.hourlyRecyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)

        val hourlyWeatherList = listOf(
            HourlyWeather("15:00", "19°", R.mipmap.ic_weather1),
            HourlyWeather("16:00", "18°", R.mipmap.ic_weather1),
            HourlyWeather("17:00", "20°", R.mipmap.ic_weather1),
            HourlyWeather("18:00", "21°", R.mipmap.ic_weather1),
            HourlyWeather("19:00", "20°", R.mipmap.ic_weather1),
            HourlyWeather("20:00", "21°", R.mipmap.ic_weather1),

            )

        recyclerView.adapter = HourlyWeatherAdapter(hourlyWeatherList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val window = requireActivity().window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.show(WindowInsetsCompat.Type.statusBars())
    }
}
