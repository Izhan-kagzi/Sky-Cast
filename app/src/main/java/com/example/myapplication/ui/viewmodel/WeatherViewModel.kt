package com.example.myapplication.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.location.LocationTracker
import com.example.myapplication.data.model.Resource
import com.example.myapplication.data.model.WeatherResponse
import com.example.myapplication.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _weatherState = MutableStateFlow<Resource<WeatherResponse>>(Resource.Loading)
    val weatherState: StateFlow<Resource<WeatherResponse>> = _weatherState.asStateFlow()

    private val _searchedWeatherState = MutableStateFlow<Resource<WeatherResponse>?>(null)
    val searchedWeatherState: StateFlow<Resource<WeatherResponse>?> = _searchedWeatherState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private var cachedLocation: Location? = null

    init {
        loadWeatherForCurrentLocation()
    }

    fun loadWeatherForCurrentLocation() {
        viewModelScope.launch {
            _weatherState.value = Resource.Loading
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                cachedLocation = location
                val result = weatherRepository.getWeatherByLocation(location.latitude, location.longitude)
                _weatherState.value = result
            } else {
                // Fallback to default city if location permission not granted or unavailable
                val fallbackResult = weatherRepository.getWeatherByCity("London")
                _weatherState.value = fallbackResult
            }
        }
    }

    fun searchCityWeather(cityName: String) {
        if (cityName.isBlank()) {
            _searchedWeatherState.value = Resource.Error("Please enter a city name")
            return
        }

        viewModelScope.launch {
            _searchedWeatherState.value = Resource.Loading
            val result = weatherRepository.getWeatherByCity(cityName.trim())
            _searchedWeatherState.value = result
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val location = cachedLocation ?: locationTracker.getCurrentLocation()
            if (location != null) {
                cachedLocation = location
                _weatherState.value = weatherRepository.getWeatherByLocation(location.latitude, location.longitude)
            } else {
                loadWeatherForCurrentLocation()
            }
            _isRefreshing.value = false
        }
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun clearSearch() {
        _searchedWeatherState.value = null
    }
}
