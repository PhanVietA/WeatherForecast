package com.example.weatherforecastapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecastapp.model.HourlyWeather

/**
 * Adapter quản lý và hiển thị danh sách thời tiết theo giờ dạng cuộn ngang trên RecyclerView ở HomeFragment.
 */
class HourlyWeatherAdapter(
    private var items: List<HourlyWeather> = emptyList()
) : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder>() {

    // ViewHolder ánh xạ và gán dữ liệu từng ô thời tiết theo giờ (item_hourly_weather) lên giao diện
    class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textHour: TextView = itemView.findViewById(R.id.textHour)
        private val iconWeather: ImageView = itemView.findViewById(R.id.iconWeather)
        private val textTemp: TextView = itemView.findViewById(R.id.textTemp)

        // Gán dữ liệu giờ, nhiệt độ và nạp biểu tượng thời tiết qua Glide
        fun bind(item: HourlyWeather) {
            textHour.text = item.hour
            textTemp.text = item.temperature
            Glide.with(itemView.context)
                .load(item.iconUrl)
                .into(iconWeather)
        }
    }

    // Khởi tạo layout item cho từng ô thời tiết theo giờ
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_weather, parent, false)
        return HourlyViewHolder(view)
    }

    // Liên kết dữ liệu HourlyWeather vào ViewHolder
    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // Trả về số lượng phần tử trong danh sách
    override fun getItemCount(): Int = items.size

    // Cập nhật danh sách mới và làm mới RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<HourlyWeather>) {
        items = newItems
        notifyDataSetChanged()
    }
}
