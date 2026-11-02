package com.example.weatherforecastapp.model

import com.google.gson.annotations.SerializedName

data class OpenMeteoResponse(
    @SerializedName("current") val current: OpenMeteoCurrent? = null
)

data class OpenMeteoCurrent(
    @SerializedName("uv_index") val uvIndex: Double = 0.0
)
