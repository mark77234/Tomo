package com.markoala.tomoandroid.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.markoala.tomoandroid.ui.components.BottomNavigationBar
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.friends.AddFriendsScreen
import com.markoala.tomoandroid.ui.main.friends.FriendsScreen
import com.markoala.tomoandroid.ui.theme.CustomColor

// 탭 타입 정의
enum class BottomTab(val label: String) {
    Home("홈"),
    Friends("친구목록"),
    Profile("내정보"),
    Settings("설정")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onSignOut: () -> Unit) {
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val user = firebaseAuth.currentUser
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }
    var routingAddFriends by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        user?.let {
            firestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { doc ->
                    name = doc.getString("name") ?: ""
                    email = doc.getString("email") ?: ""
                    userId = doc.getString("uid") ?: ""
                }
        }
    }
    Scaffold(
        topBar = {
            if (!routingAddFriends) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .background(CustomColor.white),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomText(
                        text = "토모",
                        type = CustomTextType.headlineSmall
                    )
                    CustomText(
                        text = "친구와의 우정을 기록하세요",
                        type = CustomTextType.bodyMedium,
                        color = CustomColor.gray300
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = CustomColor.gray100,
                        thickness = 1.dp
                    )
                }
            }
        },
        bottomBar = {
            if (!routingAddFriends) {
                BottomNavigationBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .background(CustomColor.white)
                .fillMaxWidth()
        ) {
            if (routingAddFriends) {
                AddFriendsScreen(
                    paddingValues = paddingValues,
                    onBackClick = { routingAddFriends = false }
                )
            } else {
                when (selectedTab) {
                    BottomTab.Home -> HomeScreen(paddingValues)
                    BottomTab.Friends -> FriendsScreen(
                        paddingValues = paddingValues,
                        onAddFriendsClick = { routingAddFriends = true }
                    )

                    BottomTab.Profile -> ProfileScreen(name, email, userId, paddingValues)
                    BottomTab.Settings -> SettingsScreen(paddingValues, onSignOut)
                }
            }
        }
    }
}
