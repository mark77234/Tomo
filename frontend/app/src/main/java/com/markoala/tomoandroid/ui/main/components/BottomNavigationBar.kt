package com.markoala.tomoandroid.ui.main.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.BottomTab
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun BottomNavigationBar(selectedTab: BottomTab, onTabSelected: (BottomTab) -> Unit) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = CustomColor.white,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        BottomTab.entries.forEach { tab ->
            val selected = selectedTab == tab

            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        painter = painterResource(id = tab.iconRes),
                        contentDescription = tab.label,
                        tint = if (selected) CustomColor.primary else CustomColor.gray500, // 🔥 아이콘 색상 변경
                    )
                },
                label = {
                    CustomText(
                        text = tab.label,
                        type = CustomTextType.label,
                        color = if (selected) CustomColor.primary else CustomColor.gray500 // 🔥 텍스트 색상 변경
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CustomColor.primary,
                    unselectedIconColor = CustomColor.gray500,
                    selectedTextColor = CustomColor.primary,
                    unselectedTextColor = CustomColor.gray500,
                    indicatorColor = Color.Transparent   // 선택 배경 제거
                )
            )
        }
    }
}
