package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object LocationPermission : Screen("location_permission")
    object WeatherHome : Screen("weather_home")
    object SearchWeather : Screen("search_weather")
}
