package com.markoala.tomoandroid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.markoala.tomoandroid.ui.home.HomeScreen
import com.markoala.tomoandroid.ui.login.LoginScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Profile : Screen("home")
}

@Composable
fun AppNavHost(navController: NavHostController, isSignedIn: Boolean) {
    NavHost(
        navController = navController,
        startDestination = if (isSignedIn) Screen.Profile.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Profile.route) {
            HomeScreen(onSignOut = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Profile.route) { inclusive = true }
                }
            })
        }
    }
}
