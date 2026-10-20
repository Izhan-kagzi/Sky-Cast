package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Resource
import com.example.myapplication.data.model.WeatherResponse
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.WeatherDetailCard
import com.example.myapplication.ui.components.WeatherGradientBackground
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.viewmodel.AuthViewModel
import com.example.myapplication.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    weatherViewModel: WeatherViewModel
) {
    val weatherState by weatherViewModel.weatherState.collectAsState()
    val isDarkMode by weatherViewModel.isDarkMode.collectAsState()

    val currentCondition = when (val state = weatherState) {
        is Resource.Success -> state.data.weather?.firstOrNull()?.mainCondition
        else -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SkyCast Weather", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { weatherViewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode"
                        )
                    }
                    IconButton(onClick = { weatherViewModel.refreshWeather() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.WeatherHome.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        WeatherGradientBackground(
            condition = currentCondition,
            isDarkMode = isDarkMode
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = weatherState) {
                    is Resource.Loading -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is Resource.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Unable to load weather",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                    is Resource.Success -> {
                        WeatherContent(weather = state.data)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherContent(weather: WeatherResponse) {
    val temp = weather.main?.temp?.roundToInt() ?: 0
    val feelsLike = weather.main?.feelsLike?.roundToInt() ?: 0
    val tempMin = weather.main?.tempMin?.roundToInt() ?: 0
    val tempMax = weather.main?.tempMax?.roundToInt() ?: 0
    val cityName = weather.cityName ?: "Unknown Location"
    val condition = weather.weather?.firstOrNull()?.mainCondition ?: "Clear"
    val description = weather.weather?.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: ""
    val humidity = weather.main?.humidity ?: 0
    val windSpeed = weather.wind?.speed ?: 0.0
    val pressure = weather.main?.pressure ?: 0
    val timestamp = weather.timestamp?.let { Date(it * 1000L) } ?: Date()
    val formattedTime = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()).format(timestamp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = cityName,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Last updated: $formattedTime",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.75f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "$temp°C",
            fontSize = 72.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )

        Text(
            text = condition,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        if (description.isNotEmpty()) {
            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "H: $tempMax°C",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "L: $tempMin°C",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeatherDetailCard(
                title = "Feels Like",
                value = "$feelsLike°C",
                icon = Icons.Default.Thermostat,
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                title = "Humidity",
                value = "$humidity%",
                icon = Icons.Default.WaterDrop,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeatherDetailCard(
                title = "Wind Speed",
                value = "$windSpeed m/s",
                icon = Icons.Default.Air,
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                title = "Pressure",
                value = "$pressure hPa",
                icon = Icons.Default.Compress,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
