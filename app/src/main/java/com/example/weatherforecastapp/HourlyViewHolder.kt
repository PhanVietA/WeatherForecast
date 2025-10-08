package com.example.weatherforecastapp

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecastapp.model.HourlyWeather

class HourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val textHour: TextView = view.findViewById(R.id.textHour)
    private val iconWeather: ImageView = view.findViewById(R.id.iconWeather)
    private val textTemp: TextView = view.findViewById(R.id.textTemp)

    fun bind(item: HourlyWeather) {
        textHour.text = item.hour
        textTemp.text = item.temperature
        Glide.with(itemView.context)
            .load(item.iconUrl)
            .into(iconWeather)
    }
}
