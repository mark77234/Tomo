package com.markoala.tomoandroid.navigation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.ui.main.calendar.promise.CreatePromiseScreen
import com.markoala.tomoandroid.ui.components.LoadingDialog
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.login.LoginScreen
import com.markoala.tomoandroid.ui.main.MainScreen

import com.markoala.tomoandroid.ui.main.meeting.meeting_detail.MeetingDetailScreen
import com.markoala.tomoandroid.ui.main.meeting.meeting_detail.MeetingPromiseListScreen
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.ui.unit.dp

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Profile : Screen("main")
    object MeetingDetail : Screen("meeting_detail/{moim_id}")
    object PromiseList : Screen("promise_list/{moim_id}/{moim_name}/{is_leader}")
    object CreatePromise : Screen("create_promise/{moim_id}/{moim_name}/{selected_date}")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    isSignedIn: Boolean,
    deepLinkInviteCode: String? = null,
    context: Context,
    onLoginSuccess: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val toastManager = LocalToastManager.current
    var isLoggingOut by remember { mutableStateOf(false) }

    if (isLoggingOut) {
        LoadingDialog()   // <-- 로딩 표시
    }

    NavHost(
        navController = navController,
        startDestination = if (isSignedIn) Screen.Profile.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                onSignedIn = {
                    onLoginSuccess()
                }
            )
        }
        composable(Screen.Profile.route) {
            MainScreen(
                onSignOut = {
                    scope.launch {
                        isLoggingOut = true
                        AuthManager.signOut(context)
                        isLoggingOut = false
                        onLogout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Profile.route) { inclusive = true }
                        }
                        toastManager.showInfo("로그아웃 되었습니다.")
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
                onBackClick = { navController.popBackStack() },
                onPromiseListClick = { id, moimName, isLeader ->
                    navController.navigate("promise_list/$id/${Uri.encode(moimName)}/$isLeader")
                }
            )
        }
        composable(
            route = Screen.PromiseList.route,
            arguments = listOf(
                navArgument("moim_id") { type = NavType.IntType },
                navArgument("moim_name") { type = NavType.StringType },
                navArgument("is_leader") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val moimId = backStackEntry.arguments?.getInt("moim_id") ?: 0
            val moimName = backStackEntry.arguments?.getString("moim_name").orEmpty()
            val isLeader = backStackEntry.arguments?.getBoolean("is_leader") ?: false
            MeetingPromiseListScreen(
                moimId = moimId,
                moimName = moimName,
                isLeader = isLeader,
                onBackClick = { navController.popBackStack() },
                onCreatePromiseClick = { _, _ ->
                    navController.navigate(
                        "create_promise/$moimId/${Uri.encode(moimName)}/${LocalDate.now()}"
                    )
                }
            )
        }
        composable(
            route = Screen.CreatePromise.route,
            arguments = listOf(
                navArgument("moim_id") { type = NavType.IntType },
                navArgument("moim_name") { type = NavType.StringType },
                navArgument("selected_date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val moimId = backStackEntry.arguments?.getInt("moim_id") ?: 0
            val selectedDate = runCatching {
                LocalDate.parse(backStackEntry.arguments?.getString("selected_date").orEmpty())
            }.getOrDefault(LocalDate.now())
            CreatePromiseScreen(
                paddingValues = PaddingValues(0.dp),
                selectedDate = selectedDate,
                onBackClick = { navController.popBackStack() },
                initialMoimId = moimId,
                onSuccess = { navController.popBackStack() }
            )
        }
    }
}
