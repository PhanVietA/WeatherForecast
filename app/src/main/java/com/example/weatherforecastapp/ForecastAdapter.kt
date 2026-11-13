package com.example.weatherforecastapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecastapp.model.DailyForecast

/**
 * Adapter quản lý và hiển thị danh sách dự báo thời tiết theo ngày trên RecyclerView ở WeatherDetailFragment.
 */
class ForecastAdapter(
    private var items: List<DailyForecast> = emptyList()
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    // ViewHolder ánh xạ các View của từng item_forecast
    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgWeatherIcon: ImageView = itemView.findViewById(R.id.imgWeatherIcon)
        val tvHour: TextView = itemView.findViewById(R.id.tvHour)
        val tvTemp: TextView = itemView.findViewById(R.id.tvTemp)

        fun bind(item: DailyForecast) {
            tvHour.text = item.time
            tvTemp.text = item.temperature
            if (!item.iconUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(item.iconUrl)
                    .into(imgWeatherIcon)
            } else {
                imgWeatherIcon.setImageResource(item.iconResId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<DailyForecast>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
