package com.victorkirui.meetnote.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.victorkirui.meetnote.R

@Composable
fun ProfileSetupRoute(
    onSaveProfile: () -> Unit = {}
) {
    ProfileSetupScreen(onSaveProfile = onSaveProfile)
}

@Composable
private fun ProfileSetupScreen(
    modifier: Modifier = Modifier,
    onSaveProfile: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    val primaryColor = Color(0xFF6366F1) // Indigo from Onboarding
    val secondaryColor = Color(0xFF818CF8)
    val backgroundColor = Color(0xFFF8FAFC)
    val lightTextColor = Color(0xFF64748B)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor, secondaryColor)
                    )
                )
                .padding(vertical = 48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.profile_setup_title),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.profile_setup_subtitle),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            ProfileInputField(
                label = stringResource(R.string.profile_setup_full_name),
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = stringResource(R.string.profile_setup_placeholder_name),
                icon = R.drawable.ic_person,
                tint = primaryColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileInputField(
                label = stringResource(R.string.profile_setup_phone_number),
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = stringResource(R.string.profile_setup_placeholder_phone),
                icon = R.drawable.ic_phone,
                tint = primaryColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileInputField(
                label = stringResource(R.string.profile_setup_email_address),
                value = emailAddress,
                onValueChange = { emailAddress = it },
                placeholder = stringResource(R.string.profile_setup_placeholder_email),
                icon = R.drawable.ic_email,
                tint = primaryColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileInputField(
                label = stringResource(R.string.profile_setup_organization),
                value = organization,
                onValueChange = { organization = it },
                placeholder = stringResource(R.string.profile_setup_placeholder_org),
                icon = R.drawable.ic_business,
                tint = primaryColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileInputField(
                label = stringResource(R.string.profile_setup_role),
                value = role,
                onValueChange = { role = it },
                placeholder = stringResource(R.string.profile_setup_placeholder_role),
                icon = R.drawable.ic_work,
                trailingIcon = R.drawable.ic_chevron_down,
                tint = primaryColor
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onSaveProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                )
            ) {
                Text(
                    text = stringResource(R.string.profile_setup_save),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.profile_setup_disclaimer),
                color = lightTextColor,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: Int,
    tint: Color,
    modifier: Modifier = Modifier,
    trailingIcon: Int? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = tint.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = trailingIcon?.let {
                {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = tint
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileSetupScreenPreview() {
    MaterialTheme {
        ProfileSetupScreen()
    }
}
