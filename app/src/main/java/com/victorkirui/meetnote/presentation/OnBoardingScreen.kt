package com.victorkirui.meetnote.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.victorkirui.meetnote.R
import com.victorkirui.meetnote.presentation.components.GlowingIndicator

@Composable
fun OnBoardingScreenRoute(
    onGetStartedClick: () -> Unit = {},
    onAlreadyHaveAccountClick: () -> Unit = {}
) {
    OnBoardingScreen(
        onGetStartedClick = onGetStartedClick,
        onAlreadyHaveAccountClick = onAlreadyHaveAccountClick
    )
}

@Composable
private fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit = {},
    onAlreadyHaveAccountClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.onboarding_welcome),
                color = Color(0xFF6366F1),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.onboarding_title),
                color = Color(0xFF0F172A),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.onboarding_description),
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                lineHeight = 22.sp,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        FeatureCard(
            title = stringResource(R.string.onboarding_feature1_title),
            description = stringResource(R.string.onboarding_feature1_desc),
            icon = R.drawable.qr_code,
            iconBackground = Color(0xFFEEF2FF),
            iconTint = Color(0xFF6366F1),
            indicatorColor = Color(0xFF6366F1)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FeatureCard(
            title = stringResource(R.string.onboarding_feature2_title),
            description = stringResource(R.string.onboarding_feature2_desc),
            icon = R.drawable.ic_scan,
            iconBackground = Color(0xFFECFDF5),
            iconTint = Color(0xFF10B981),
            indicatorColor = Color(0xFF10B981)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FeatureCard(
            title = stringResource(R.string.onboarding_feature3_title),
            description = stringResource(R.string.onboarding_feature3_desc),
            icon = R.drawable.ic_list,
            iconBackground = Color(0xFFF5F3FF),
            iconTint = Color(0xFF8B5CF6),
            indicatorColor = Color(0xFF8B5CF6)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onGetStartedClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6366F1)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.onboarding_button_text),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_forward),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: Int,
    iconBackground: Color,
    iconTint: Color,
    indicatorColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFC)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color(0xFF0F172A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            GlowingIndicator(
                color = indicatorColor,
                dotSize = 8.dp,
                glowRadius = 4.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnBoardingScreenPreview() {
    MaterialTheme {
        OnBoardingScreen()
    }
}

@Preview(device = "spec:width=673dp,height=841dp")
@Composable
private fun OnBoardingScreenFoldablePreview(){
    MaterialTheme{
        OnBoardingScreen()
    }
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun OnBoardingScreenTabletPreview(){
    MaterialTheme{
        OnBoardingScreen()
    }
}
