package com.markoala.tomoandroid.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
