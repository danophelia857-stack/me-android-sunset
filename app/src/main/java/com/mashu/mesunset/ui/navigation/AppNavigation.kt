package com.mashu.mesunset.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mashu.mesunset.data.repository.MESunsetRepository
import com.mashu.mesunset.ui.screens.home.HomeScreen
import com.mashu.mesunset.ui.screens.login.LoginScreen
import com.mashu.mesunset.ui.viewmodels.MainViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Packages : Screen("packages")
    object Purchase : Screen("purchase")
    object History : Screen("history")
    object Account : Screen("account")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = MESunsetRepository(context)
    val viewModel = MainViewModel(repository)
    
    val startDestination = if (repository.getActiveUser() != null) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
