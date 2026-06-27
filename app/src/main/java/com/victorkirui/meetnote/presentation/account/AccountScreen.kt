package com.victorkirui.meetnote.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.victorkirui.meetnote.domain.model.ProfileDataModel
import com.victorkirui.meetnote.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AccountRoute(
    onBackClick: () -> Unit,
    onWorkProfileClick: () -> Unit,
    onSocialProfileClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    viewModel: AccountViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AccountScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onWorkProfileClick = onWorkProfileClick,
        onSocialProfileClick = onSocialProfileClick,
        onPrivacyPolicyClick = onPrivacyPolicyClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    uiState: AccountUiState,
    onBackClick: () -> Unit,
    onWorkProfileClick: () -> Unit,
    onSocialProfileClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    val darkBlue = Color(0xFF1E1B4B)
    val backgroundColor = Color(0xFFF8FAFC)
    val cardColor = Color(0xFFF3EDF7)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBlue),
                title = { 
                    Text(
                        "Account", 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back", 
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(darkBlue)
                    .padding(bottom = 32.dp, top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Profile Image
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            val profile = uiState.workProfile ?: uiState.socialProfile
                            if (profile?.profilePicture != null) {
                                AsyncImage(
                                    model = profile.profilePicture,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    val initials = profile?.fullName?.split(" ")
                                        ?.filter { it.isNotEmpty() }
                                        ?.joinToString("") { it.take(1) }
                                        ?.uppercase() ?: "U"
                                    Text(
                                        text = initials,
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = Color(0xFF334155),
                            border = androidx.compose.foundation.BorderStroke(2.dp, darkBlue)
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Edit photo",
                                tint = Color.White,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = uiState.workProfile?.fullName ?: uiState.socialProfile?.fullName ?: "User Profile",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    val profileCount = listOfNotNull(uiState.workProfile, uiState.socialProfile).size
                    Text(
                        text = "$profileCount profiles set up",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Profiles Section
            SectionLabel(text = "PROFILES")
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfileItem(
                type = "WORK",
                name = uiState.workProfile?.fullName ?: "Setup Work Profile",
                subtitle = if (uiState.workProfile != null) {
                    val role = uiState.workProfile.role.orEmpty()
                    val org = uiState.workProfile.organization.orEmpty()
                    if (role.isNotEmpty() && org.isNotEmpty()) "$role · $org" else role.ifEmpty { org }.ifEmpty { "Work details not set" }
                } else "Tap to setup your professional profile",
                icon = Icons.Default.Work,
                iconContainerColor = Color(0xFF1E1B4B),
                onClick = onWorkProfileClick,
                backgroundColor = cardColor
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfileItem(
                type = "SOCIAL",
                name = uiState.socialProfile?.fullName ?: "Setup Social Profile",
                subtitle = if (uiState.socialProfile != null) {
                    uiState.socialProfile.userName.ifEmpty { "Social details not set" }
                } else "Tap to setup your social profile",
                icon = Icons.Default.Person,
                iconContainerColor = Color(0xFFB34E3C),
                onClick = onSocialProfileClick,
                backgroundColor = cardColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            // About Section
            SectionLabel(text = "ABOUT")
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    AboutItem(
                        icon = Icons.Outlined.Info,
                        label = "App Version",
                        value = "1.0.0",
                        showArrow = false
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF1F5F9))
                    AboutItem(
                        icon = Icons.Outlined.VerifiedUser,
                        label = "Privacy Policy",
                        onClick = onPrivacyPolicyClick
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 24.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B)
    )
}

@Composable
fun ProfileItem(
    type: String,
    name: String,
    subtitle: String,
    icon: ImageVector,
    iconContainerColor: Color,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = type,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFCBD5E1)
            )
        }
    }
}

@Composable
fun AboutItem(
    icon: ImageVector,
    label: String,
    value: String? = null,
    showArrow: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF1E1B4B), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B)
        )
        if (value != null) {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
        }
        if (showArrow) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFCBD5E1)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountScreenPreview() {
    AppTheme {
        AccountScreen(
            uiState = AccountUiState(isLoading = false),
            onBackClick = {},
            onWorkProfileClick = {},
            onSocialProfileClick = {},
            onPrivacyPolicyClick = {}
        )
    }
}
