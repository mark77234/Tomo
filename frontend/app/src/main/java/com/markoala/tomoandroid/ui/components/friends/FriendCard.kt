package com.markoala.tomoandroid.ui.components.friends

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.data.model.FriendProfile
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.DashedCircleBorder
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.utils.calculateDate


@Composable
fun FriendCard(friend: FriendProfile) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CustomColor.white),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                CustomColor.gray100,
                androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
        ) {
            Row {
                DashedCircleBorder(
                    modifier = Modifier.padding(end = 10.dp),
                    size = 48.dp,
                    borderColor = CustomColor.gray100,
                    borderWidth = 2.dp
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.markoala.tomoandroid.R.drawable.ic_profile),
                        contentDescription = null,
                        tint = CustomColor.gray200,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                    )
                }
                Column {
                    CustomText(
                        text = friend.name,
                        type = CustomTextType.titleMedium,
                        color = CustomColor.black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                            4.dp
                        )
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.markoala.tomoandroid.R.drawable.ic_email),
                            contentDescription = null,
                            tint = CustomColor.gray200,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(12.dp)
                        )
                        CustomText(
                            text = friend.email,
                            type = CustomTextType.bodyMedium,
                            color = CustomColor.gray200,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
            ) {

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = CustomColor.gray50,
                    thickness = 1.dp
                )
                Row(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        4.dp
                    )
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.markoala.tomoandroid.R.drawable.ic_time),
                        contentDescription = null,
                        tint = CustomColor.gray200,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(12.dp)
                    )
                    CustomText(
                        text = "우정 기간: " + calculateDate(friend.friendSince),
                        type = CustomTextType.bodyMedium,
                        color = CustomColor.gray200,
                        fontSize = 12.sp
                    )
                }
            }

        }
    }
}