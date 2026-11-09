package com.example.weatherforecastapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapp.model.HourlyWeather

/**
 * Adapter quản lý và hiển thị danh sách thời tiết theo giờ dạng cuộn ngang trên RecyclerView ở HomeFragment.
 */
class HourlyWeatherAdapter(
    private var items: List<HourlyWeather> = emptyList()
) : RecyclerView.Adapter<HourlyViewHolder>() {

    // Khởi tạo layout item cho từng ô thời tiết theo giờ
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_weather, parent, false)
        return HourlyViewHolder(view)
    }

    // Liên kết dữ liệu HourlyWeather vào ViewHolder
    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
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
