package com.example.weatherforecastapp.model

import com.google.gson.annotations.SerializedName

data class AirQualityResponse(
    @SerializedName("coord") val coord: CoordAirQuality? = null,
    @SerializedName("list") val list: List<AirQualityItem> = emptyList()
)

data class CoordAirQuality(
    @SerializedName("lon") val lon: Double? = null,
    @SerializedName("lat") val lat: Double? = null
)

data class AirQualityItem(
    @SerializedName("main") val main: AqiMain? = null,
    @SerializedName("components") val components: Map<String, Double>? = null,
    @SerializedName("dt") val dt: Long? = null
)
data class AqiMain(
    @SerializedName("aqi") val aqi: Int = 1
)
