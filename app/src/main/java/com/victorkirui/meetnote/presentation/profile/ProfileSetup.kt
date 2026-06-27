package com.victorkirui.meetnote.presentation.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.victorkirui.meetnote.presentation.state.SocialLinkState
import com.victorkirui.meetnote.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileSetupRoute(
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    viewModel: ProfileSetupViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                is ProfileScreenEvent.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is ProfileScreenEvent.NavigateForward -> {
                    onSaveSuccess()
                }
            }
        }
    }

    ProfileSetupScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSaveClick = viewModel::onSaveWorkProfile,
        onSkipClick = onSaveSuccess,
        onNameChange = viewModel::onNameChanged,
        onEmailChange = viewModel::onEmailChanged,
        onPhoneChange = viewModel::onPhoneNumberChanged,
        onOrgChange = viewModel::onOrganizationChanged,
        onRoleChange = viewModel::onRoleChanged,
        onAddSocialLink = viewModel::onAddSocialLink,
        onDeleteSocialLink = viewModel::onDeleteSocialLink,
        onSocialLinkChange = viewModel::onSocialLinkChanged,
        onProfilePictureSelected = viewModel::onProfilePictureSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileSetupScreen(
    uiState: ProfileUiState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onSkipClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onOrgChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onAddSocialLink: () -> Unit,
    onDeleteSocialLink: (Int) -> Unit,
    onSocialLinkChange: (Int, SocialLinkState) -> Unit,
    onProfilePictureSelected: (String) -> Unit
) {
    val lightGray = Color(0xFF64748B)
    val midnightBlue = Color(0xFF1E1B4B)
    val backgroundColor = Color(0xFFFAF8FE)

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onProfilePictureSelected(it.toString()) }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Work Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = midnightBlue) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        TextButton(onClick = onBackClick) {
                            Text("Cancel", color = midnightBlue, fontWeight = FontWeight.Bold)
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
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Profile Picture
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(color = Color(0xFFE2E8F0))
                            .border(width = 2.dp, shape = CircleShape, color = if(uiState.profilePictureHasError) Color.Red else Color.Transparent)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.profilePictureUri.isNotEmpty()) {
                            AsyncImage(
                                model = uiState.profilePictureUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = lightGray
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(midnightBlue)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Add photo", fontSize = 12.sp, color = midnightBlue, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(32.dp))

                // Personal Information Section
                SectionHeader("PERSONAL INFORMATION")
                Spacer(modifier = Modifier.height(16.dp))
                ProfileTextField(
                    value = uiState.fullName,
                    onValueChange = onNameChange,
                    icon = Icons.Default.Person,
                    trailingLabel = "Required",
                    placeholder = "Full Name",
                    hasError = uiState.fullNameHasError
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    icon = Icons.Default.Email,
                    placeholder = "Email Address",
                    hasError = uiState.emailHasError
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileTextField(
                    value = uiState.phoneNumber,
                    onValueChange = onPhoneChange,
                    icon = Icons.Default.Phone,
                    placeholder = "Phone Number",
                    hasError = uiState.phoneNumberHasError
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Professional Section
                SectionHeader("PROFESSIONAL")
                Spacer(modifier = Modifier.height(16.dp))
                ProfileTextField(
                    value = uiState.organization,
                    onValueChange = onOrgChange,
                    icon = Icons.Default.Business,
                    placeholder = "Organization",
                    hasError = false
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileTextField(
                    value = uiState.role,
                    onValueChange = onRoleChange,
                    icon = Icons.Default.Work,
                    placeholder = "Role",
                    hasError = false
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Social Links Section
                SectionHeader("SOCIAL LINKS")
                Spacer(modifier = Modifier.height(16.dp))
                uiState.socialLinks.forEachIndexed { index, link ->
                    SocialLinkItem(
                        link = link,
                        onLinkChange = { updatedLink -> onSocialLinkChange(index, updatedLink) },
                        onDelete = { onDeleteSocialLink(index) },
                        accentColor = midnightBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                TextButton(
                    onClick = onAddSocialLink,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = midnightBlue)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add social link", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = midnightBlue,
                        disabledContainerColor = Color(0xFFE2E8F0)
                    ),
                    enabled = uiState.canSaveForm
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        val buttonText = if (uiState.fullName.isEmpty()) "Save Profile" else "Update Profile"
                        Text(buttonText, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.fullName.isEmpty()) {
                    TextButton(
                        onClick = onSkipClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Skip", color = midnightBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    trailingLabel: String? = null,
    placeholder: String,
    hasError: Boolean
) {
    val midnightBlue = Color(0xFF1E1B4B)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
        placeholder = { Text(text = placeholder, color = Color(0xFF94A3B8)) },
        trailingIcon = {
            if (trailingLabel != null) {
                Text(
                    text = trailingLabel,
                    fontSize = 11.sp,
                    color = Color(0xFFB34E3C),
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = midnightBlue,
            unfocusedTextColor = midnightBlue,
            focusedBorderColor = Color(0xFFE2E8F0),
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true,
        isError = hasError
    )
}

@Composable
private fun SocialLinkItem(
    link: SocialLinkState,
    onLinkChange: (SocialLinkState) -> Unit,
    onDelete: () -> Unit,
    accentColor: Color
) {
    var expanded by remember { mutableStateOf(false) }
    val platforms = listOf("LinkedIn", "X (Twitter)", "Instagram", "GitHub", "Website")
    val midnightBlue = Color(0xFF1E1B4B)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Platform Icon Box (Clickable for Selection)
        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF3EDF7))
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = when(link.platform.lowercase()) {
                        "linkedin" -> Icons.Default.Link
                        "github" -> Icons.Default.Link
                        "instagram" -> Icons.Default.Link
                        "x (twitter)", "x", "twitter" -> Icons.Default.Link
                        else -> Icons.Default.Link
                    },
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = accentColor
                )
                Text(
                    text = link.platform.ifEmpty { "Platform" },
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                platforms.forEach { platform ->
                    DropdownMenuItem(
                        text = { Text(platform, color = midnightBlue) },
                        onClick = {
                            onLinkChange(link.copy(platform = platform))
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Input Box
        OutlinedTextField(
            value = link.url,
            onValueChange = { onLinkChange(link.copy(url = it)) },
            modifier = Modifier.weight(1f),
            placeholder = { Text("username or link", fontSize = 14.sp, color = Color(0xFF94A3B8)) },
            trailingIcon = {
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp), tint = Color(0xFF64748B))
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = midnightBlue,
                unfocusedTextColor = midnightBlue,
                focusedBorderColor = Color(0xFFE2E8F0),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileSetupPreview() {
    AppTheme {
        ProfileSetupScreen(
            uiState = ProfileUiState(
                fullName = "Alex Johnson",
                email = "alex.johnson@acmecorp.com",
                phoneNumber = "+1 (555) 234-5678",
                organization = "Acme Corp",
                role = "Senior Product Designer",
                socialLinks = listOf(
                    SocialLinkState(platform = "LinkedIn", url = "linkedin.com/in/alexjohnson"),
                    SocialLinkState(platform = "X (Twitter)", url = "@alexjohnson")
                )
            ),
            onSaveClick = {},
            onSkipClick = {},
            onNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onOrgChange = {},
            onRoleChange = {},
            onAddSocialLink = {},
            onDeleteSocialLink = {},
            onSocialLinkChange = { _, _ -> },
            onProfilePictureSelected = {},
            onBackClick = {}
        )
    }
}
