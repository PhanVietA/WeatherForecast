package com.example.weatherforecast

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.model.HourlyWeather
import com.example.weatherforecastapp.R

class A2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_a2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.hourlyRecyclerView)

        // Bắt buộc set layout manager HORIZONTAL ở code (ổn định nhất)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Tối ưu: tránh nested scroll nếu parent scrollable
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)

        // Dummy data kiểm tra
        val hourlyWeatherList = listOf(
            HourlyWeather("15:00", "19°", R.mipmap.ic_weather1),
            HourlyWeather("16:00", "18°", R.mipmap.ic_weather1),
            HourlyWeather("17:00", "20°", R.mipmap.ic_weather1),
            HourlyWeather("18:00", "21°", R.mipmap.ic_weather1),
        )

        recyclerView.adapter = HourlyWeatherAdapter(hourlyWeatherList)

        Log.d("A2Fragment", "LM class = ${recyclerView.layoutManager?.javaClass?.simpleName}")
        Log.d("A2Fragment", "adapter itemCount = ${recyclerView.adapter?.itemCount}")
    }
}
