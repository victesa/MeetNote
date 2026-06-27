package com.victorkirui.meetnote.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.victorkirui.meetnote.R

import com.victorkirui.meetnote.ui.theme.AppTheme

@Composable
fun OnBoardingScreen2Route(
    onNextClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    OnBoardingScreen2(
        onNextClick = onNextClick,
        onBackClick = onBackClick
    )
}

@Composable
private fun OnBoardingScreen2(
    modifier: Modifier = Modifier,
    onNextClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF1F3F4), Color(0xFFFFFFFF)),
        endY = .6f
    )
    val midnightBlue = Color(0xFF1E1B4B)
    val lightGray = Color(0xFF64748B)
    val cardBackground = Color.White

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//         Top Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Progress indicators
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(Color(0xFFE76F51))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(Color(0xFFE76F51))
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Illustration Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.6f)
                .clipToBounds(),
            contentAlignment = Alignment.TopCenter
        ) {
            // 1. Phone Image Asset
            Image(
                painter = painterResource(R.drawable.onboarding_pic1),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            )

        }

        // Text Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.onboarding_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.onboarding_description),
                fontSize = 15.sp,
                color = lightGray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))

        // Next Button
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(8.dp, RoundedCornerShape(30.dp), spotColor = midnightBlue),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE76F51))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.onboarding2_button_next),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_forward),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun EventCard(
    initials: String,
    name: String,
    role: String,
    badgeText: String,
    avatarColor: Color = Color(0xFF6366F1),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(320.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left vertical bar
            Box(
                modifier = Modifier
                    .width(1.5.dp)
                    .height(32.dp)
                    .background(avatarColor.copy(alpha = 0.3f), RoundedCornerShape(1.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Avatar with initials
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(2.dp, avatarColor.copy(alpha = 0.2f), CircleShape)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = role,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ProfileBubbles() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        // Main large bubble
        Bubble(size = 80.dp, color = Color(0xFFE2E8F0))

        // Overlapping small bubbles
        Bubble(size = 42.dp, color = Color(0xFFCBD5E1), modifier = Modifier.offset(x = (-60).dp, y = (-35).dp))
        Bubble(size = 46.dp, color = Color(0xFF94A3B8), modifier = Modifier.offset(x = 70.dp, y = (-25).dp))
        Bubble(size = 36.dp, color = Color(0xFF64748B), modifier = Modifier.offset(x = (-80).dp, y = 25.dp))
        Bubble(size = 40.dp, color = Color(0xFF475569), modifier = Modifier.offset(x = 55.dp, y = 45.dp))
        Bubble(size = 32.dp, color = Color(0xFF334155), modifier = Modifier.offset(x = (-35).dp, y = 70.dp))
        Bubble(size = 30.dp, color = Color(0xFF1E293B), modifier = Modifier.offset(x = 90.dp, y = 35.dp))
        Bubble(size = 38.dp, color = Color(0xFFE2E8F0), modifier = Modifier.offset(x = (-85).dp, y = (-5).dp))

        // Sparkles (using star icon)
        Icon(
            painter = painterResource(R.drawable.ic_star),
            contentDescription = null,
            tint = Color(0xFFF97316).copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp).offset(x = (-110).dp, y = (-50).dp)
        )
        Icon(
            painter = painterResource(R.drawable.ic_star),
            contentDescription = null,
            tint = Color(0xFF38BDF8).copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp).offset(x = 35.dp, y = (-85).dp)
        )
        Icon(
            painter = painterResource(R.drawable.ic_star),
            contentDescription = null,
            tint = Color(0xFF94A3B8).copy(alpha = 0.3f),
            modifier = Modifier.size(14.dp).offset(x = (-70).dp, y = (-90).dp)
        )
    }
}

@Composable
private fun Bubble(size: androidx.compose.ui.unit.Dp, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_person),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(size * 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnBoardingScreen2Preview() {
    AppTheme(dynamicColor = false) {
        OnBoardingScreen2()
    }
}
