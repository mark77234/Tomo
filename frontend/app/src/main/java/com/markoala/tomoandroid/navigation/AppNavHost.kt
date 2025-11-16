package com.markoala.tomoandroid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.markoala.tomoandroid.ui.login.LoginScreen
import com.markoala.tomoandroid.ui.main.MainScreen
import com.markoala.tomoandroid.ui.main.meeting.meeting_detail.MeetingDetailScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Profile : Screen("main")
    object MeetingDetail : Screen("meeting_detail/{moimTitle}") {
        fun createRoute(moimTitle: String) = "meeting_detail/$moimTitle"
    }
}

@Composable
fun AppNavHost(navController: NavHostController, isSignedIn: Boolean, deepLinkInviteCode: String? = null) {
    NavHost(
        navController = navController,
        startDestination = if (isSignedIn) Screen.Profile.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Profile.route) {
            MainScreen(
                navController = navController,
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                deepLinkInviteCode = deepLinkInviteCode
            )
        }
        composable(
            route = Screen.MeetingDetail.route,
            arguments = listOf(navArgument("moimTitle") { type = NavType.StringType })
        ) { backStackEntry ->
            val moimTitle = backStackEntry.arguments?.getString("moimTitle") ?: ""
            MeetingDetailScreen(
                moimTitle = moimTitle,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
