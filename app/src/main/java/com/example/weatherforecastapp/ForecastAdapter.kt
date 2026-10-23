package com.example.weatherforecastapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecastapp.model.Forecast

class ForecastAdapter(
    private var items: List<Forecast> = emptyList()
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgWeatherIcon: ImageView = itemView.findViewById(R.id.imgWeatherIcon)
        val tvHour: TextView = itemView.findViewById(R.id.tvHour)
        val tvTemp: TextView = itemView.findViewById(R.id.tvTemp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = items[position]
        holder.tvHour.text = item.time
        holder.tvTemp.text = item.temperature

        if (!item.iconUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.iconUrl)
                .placeholder(item.iconResId)
                .into(holder.imgWeatherIcon)
        } else {
            holder.imgWeatherIcon.setImageResource(item.iconResId)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Forecast>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
