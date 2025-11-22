package com.markoala.tomoandroid.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.login.LoginScreen
import com.markoala.tomoandroid.ui.main.MainScreen

import com.markoala.tomoandroid.ui.main.meeting.meeting_detail.MeetingDetailScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Profile : Screen("main")
    object MeetingDetail : Screen("meeting_detail/{moim_id}") {
//        fun createRoute(moim_id: Int) = "meeting_detail/$moim_id"
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    isSignedIn: Boolean,
    deepLinkInviteCode: String? = null,
    context: Context
) {
    val scope = rememberCoroutineScope()
    val toastManager = LocalToastManager.current
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
                    scope.launch {
                        AuthManager.signOut(context)
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Profile.route) { inclusive = true }
                            toastManager.showInfo("로그아웃 되었습니다.")
                        }
                    }
                },
                deepLinkInviteCode = deepLinkInviteCode
            )
        }
        composable(
            route = Screen.MeetingDetail.route,
            arguments = listOf(navArgument("moim_id") { type = NavType.IntType })
        ) { backStackEntry ->
            val moimId = backStackEntry.arguments?.getInt("moim_id") ?: 0
            MeetingDetailScreen(
                moimId = moimId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
