package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Resource
import com.example.myapplication.data.model.WeatherResponse

interface WeatherRepository {
    suspend fun getWeatherByLocation(lat: Double, lon: Double): Resource<WeatherResponse>
    suspend fun getWeatherByCity(cityName: String): Resource<WeatherResponse>
}
