package com.example.weatherforecastapp.model

import com.google.gson.annotations.SerializedName

data class AirQualityResponse(
    @SerializedName("coord") val coord: List<Double>?,
    @SerializedName("list") val list: List<AirQualityItem>
)

data class AirQualityItem(
    @SerializedName("main") val main: AqiMain,
    @SerializedName("components") val components: Map<String, Double>,
    @SerializedName("dt") val dt: Long
)

data class AqiMain(
    @SerializedName("aqi") val aqi: Int // Giá trị AQI từ 1 đến 5
)
