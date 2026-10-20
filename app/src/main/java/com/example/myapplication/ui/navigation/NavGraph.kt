package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.ForgotPasswordScreen
import com.example.myapplication.ui.screens.LocationPermissionScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.RegisterScreen
import com.example.myapplication.ui.screens.SearchWeatherScreen
import com.example.myapplication.ui.screens.SplashScreen
import com.example.myapplication.ui.screens.WeatherHomeScreen
import com.example.myapplication.ui.viewmodel.AuthViewModel
import com.example.myapplication.ui.viewmodel.WeatherViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    weatherViewModel: WeatherViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.LocationPermission.route) {
            LocationPermissionScreen(
                navController = navController,
                weatherViewModel = weatherViewModel
            )
        }

        composable(Screen.WeatherHome.route) {
            WeatherHomeScreen(
                navController = navController,
                authViewModel = authViewModel,
                weatherViewModel = weatherViewModel
            )
        }

        composable(Screen.SearchWeather.route) {
            SearchWeatherScreen(
                navController = navController,
                weatherViewModel = weatherViewModel
            )
        }
    }
}
