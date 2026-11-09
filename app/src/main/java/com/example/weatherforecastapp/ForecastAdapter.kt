package com.example.weatherforecastapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
    }

    // Khởi tạo layout item cho danh sách dự báo ngày
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    // Liên kết dữ liệu DailyForecast lên ViewHolder
    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = items[position]
        holder.imgWeatherIcon.setImageResource(item.iconResId)
        holder.tvHour.text = item.time
        holder.tvTemp.text = item.temperature
    }

    // Trả về số lượng phần tử
    override fun getItemCount(): Int = items.size

    // Cập nhật danh sách mới và làm mới RecyclerView
    fun updateData(newItems: List<DailyForecast>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
