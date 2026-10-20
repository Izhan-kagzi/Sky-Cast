package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("coord") val coord: Coord? = null,
    @SerializedName("weather") val weather: List<WeatherInfo>? = null,
    @SerializedName("main") val main: MainInfo? = null,
    @SerializedName("wind") val wind: WindInfo? = null,
    @SerializedName("sys") val sys: SysInfo? = null,
    @SerializedName("name") val cityName: String? = null,
    @SerializedName("dt") val timestamp: Long? = null
)

data class Coord(
    @SerializedName("lat") val lat: Double = 0.0,
    @SerializedName("lon") val lon: Double = 0.0
)

data class WeatherInfo(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("main") val mainCondition: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("icon") val icon: String = ""
)

data class MainInfo(
    @SerializedName("temp") val temp: Double = 0.0,
    @SerializedName("feels_like") val feelsLike: Double = 0.0,
    @SerializedName("temp_min") val tempMin: Double = 0.0,
    @SerializedName("temp_max") val tempMax: Double = 0.0,
    @SerializedName("pressure") val pressure: Int = 0,
    @SerializedName("humidity") val humidity: Int = 0
)

data class WindInfo(
    @SerializedName("speed") val speed: Double = 0.0,
    @SerializedName("deg") val deg: Int = 0
)

data class SysInfo(
    @SerializedName("country") val country: String = "",
    @SerializedName("sunrise") val sunrise: Long = 0L,
    @SerializedName("sunset") val sunset: Long = 0L
)
