package com.markoala.tomoandroid.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

// 탭 타입 정의
enum class BottomTab(val label: String) {
    Home("홈"),
    Friends("친구목록"),
    Profile("내정보"),
    Settings("설정")
}

@Composable
fun BottomNavigationBar(selectedTab: BottomTab, onTabSelected: (BottomTab) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == BottomTab.Home,
            onClick = { onTabSelected(BottomTab.Home) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "홈") },
            label = { Text("홈") }
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.Friends,
            onClick = { onTabSelected(BottomTab.Friends) },
            icon = { Icon(Icons.Filled.Face, contentDescription = "친구목록") },
            label = { Text("친구목록") }
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.Profile,
            onClick = { onTabSelected(BottomTab.Profile) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "내정보") },
            label = { Text("내정보") }
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.Settings,
            onClick = { onTabSelected(BottomTab.Settings) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "설정") },
            label = { Text("설정") }
        )
    }
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
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

        },
        bottomBar = {
            BottomNavigationBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { paddingValues ->
        when (selectedTab) {
            BottomTab.Home -> HomeScreen(paddingValues)
            BottomTab.Friends -> FriendsScreen(paddingValues)
            BottomTab.Profile -> ProfileScreen(name, email, userId, paddingValues)
            BottomTab.Settings -> SettingsScreen(paddingValues, onSignOut)
        }
    }
}
