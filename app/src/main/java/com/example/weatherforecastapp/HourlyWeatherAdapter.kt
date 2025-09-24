package com.example.weatherforecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.model.HourlyWeather
import com.example.weatherforecastapp.R

class HourlyWeatherAdapter(
    private val items: List<HourlyWeather>
) : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder>() {

    inner class HourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textHour: TextView = view.findViewById(R.id.textHour)
        val iconWeather: ImageView = view.findViewById(R.id.iconWeather)
        val textTemp: TextView = view.findViewById(R.id.textTemp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_weather, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = items[position]
        holder.textHour.text = item.hour
        holder.textTemp.text = item.temperature
        holder.iconWeather.setImageResource(item.iconRes)
    }

    override fun getItemCount(): Int = items.size
}
