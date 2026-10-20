package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Resource
import com.example.myapplication.data.model.WeatherResponse
import com.example.myapplication.data.remote.OpenWeatherApi
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi
) : WeatherRepository {

    override suspend fun getWeatherByLocation(lat: Double, lon: Double): Resource<WeatherResponse> {
        return try {
            val response = api.getWeatherByCoordinates(
                lat = lat,
                lon = lon,
                apiKey = OpenWeatherApi.DEFAULT_API_KEY
            )
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch weather data for location")
        }
    }

    override suspend fun getWeatherByCity(cityName: String): Resource<WeatherResponse> {
        return try {
            val response = api.getWeatherByCity(
                cityName = cityName,
                apiKey = OpenWeatherApi.DEFAULT_API_KEY
            )
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "City not found or network error")
        }
    }
}
