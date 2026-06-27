package com.victorkirui.meetnote.presentation.contacts.more_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.victorkirui.meetnote.presentation.contacts.add.AddContactEvents
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.ui.theme.AppTheme

@Composable
fun ContactMoreDetailsRoute(
    isEditMode: Boolean = false,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: ContactMoreDetailsViewModel
) {
    val uiState by viewModel.formInput.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            if (event is AddContactEvents.ShowConfirmationSheet) {
                onSaveSuccess()
            }
        }
    }

    ContactMoreDetailsScreen(
        uiState = uiState,
        isEditMode = isEditMode,
        onBackClick = onBackClick,
        onSaveClick = viewModel::onSaveContactDetails,
        onSkipClick = onSaveSuccess,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onTagChange = viewModel::onTagChange,
        onOrganizationChange = viewModel::onOrganizationChange,
        onRoleChange = viewModel::onRoleChange,
        onSocialLinkChange = viewModel::onSocialLinkChange,
        onLocationChange = viewModel::onLocationChange,
        onNotesChange = viewModel::onNotesChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactMoreDetailsScreen(
    uiState: ContactMoreDetailsUiState,
    isEditMode: Boolean = false,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onSkipClick: () -> Unit,
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPhoneNumberChange: (String) -> Unit = {},
    onTagChange: (ProfileType) -> Unit = {},
    onOrganizationChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onSocialLinkChange: (Int, String) -> Unit,
    onLocationChange: (String) -> Unit,
    onNotesChange: (String) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = { Text(if (isEditMode) "Edit Contact" else "More Details", fontWeight = FontWeight.Bold, color = Color(0xFF1E1B4B)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E1B4B))
                    }
                },
                actions = {
                    TextButton(onClick = onSaveClick) {
                        Text("Save", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Bold)
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isEditMode) "Edit contact details" else "Optional details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B4B)
                )
                Text(
                    text = if (isEditMode) "Update information for this contact." else "Add context, links, and notes when you have time.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }

            if (!isEditMode) {
                // Contact Summary Card (ReadOnly in Non-Edit Mode)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (uiState.tag == ProfileType.WORK) Color(0xFF0D1B4B) else Color(0xFFB34E3C))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5E1E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!uiState.profilePicture.isNullOrEmpty()) {
                                AsyncImage(
                                    model = uiState.profilePicture,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                val initials = remember(uiState.firstName, uiState.lastName) {
                                    ((uiState.firstName.take(1)) + (uiState.lastName.take(1))).uppercase()
                                }
                                Text(
                                    text = initials,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.tag == ProfileType.WORK) Color(0xFF0D1B4B) else Color(0xFFB34E3C)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.fullName,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = uiState.phoneNumber ?: uiState.email ?: "",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Surface(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (uiState.tag == ProfileType.WORK) Icons.Default.Work else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = uiState.tag.name.lowercase().replaceFirstChar { it.uppercase() },
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // Basic Info Section (Editable in Edit Mode)
                DetailSection(
                    title = "Basic Info",
                    icon = Icons.Default.Person
                ) {
                    DetailTextField(
                        label = "First Name",
                        value = uiState.firstName,
                        onValueChange = onFirstNameChange,
                        placeholder = "First name"
                    )
                    DetailTextField(
                        label = "Last Name",
                        value = uiState.lastName,
                        onValueChange = onLastNameChange,
                        placeholder = "Last name"
                    )
                    DetailTextField(
                        label = "Phone Number",
                        value = uiState.phoneNumber ?: "",
                        onValueChange = onPhoneNumberChange,
                        placeholder = "Phone number"
                    )
                    DetailTextField(
                        label = "Email Address",
                        value = uiState.email ?: "",
                        onValueChange = onEmailChange,
                        placeholder = "Email address"
                    )
                    
                    Text("Category", fontSize = 11.sp, color = Color(0xFF64748B), modifier = Modifier.padding(top = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = uiState.tag == ProfileType.WORK,
                            onClick = { onTagChange(ProfileType.WORK) },
                            label = { Text("Work") },
                            leadingIcon = if (uiState.tag == ProfileType.WORK) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                        FilterChip(
                            selected = uiState.tag == ProfileType.SOCIAL,
                            onClick = { onTagChange(ProfileType.SOCIAL) },
                            label = { Text("Social") },
                            leadingIcon = if (uiState.tag == ProfileType.SOCIAL) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }

            // Organization Section
            DetailSection(
                title = "Organization",
                icon = Icons.Default.Business
            ) {
                DetailTextField(
                    label = "Company",
                    value = uiState.organization,
                    onValueChange = onOrganizationChange,
                    placeholder = "Organization name"
                )
                DetailTextField(
                    label = "Role",
                    value = uiState.role,
                    onValueChange = onRoleChange,
                    placeholder = "Job title"
                )
            }

            // Social Links Section
            DetailSection(
                title = "Social Links",
                icon = Icons.Default.Public
            ) {
                uiState.socialLinks.forEachIndexed { index, link ->
                    DetailTextField(
                        label = link.platform,
                        value = link.url,
                        onValueChange = { onSocialLinkChange(index, it) },
                        placeholder = when (link.platform) {
                            "LinkedIn" -> "linkedin.com/in/username"
                            "Instagram" -> "@username"
                            else -> "Website or portfolio"
                        }
                    )
                }
            }

            // Location Section
            DetailSection(
                title = "Location",
                icon = Icons.Default.LocationOn
            ) {
                DetailTextField(
                    label = "Residence",
                    value = uiState.location,
                    onValueChange = onLocationChange,
                    placeholder = "City, Country"
                )
            }

            // Notes Section
            DetailSection(
                title = "Notes",
                icon = Icons.AutoMirrored.Filled.Notes
            ) {
                DetailTextField(
                    label = "Notes",
                    value = uiState.notes,
                    onValueChange = onNotesChange,
                    placeholder = "Additional context...",
                    singleLine = false,
                    minLines = 3
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF45445C))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isEditMode) "Save Changes" else "Save Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (!isEditMode) {
                    TextButton(onClick = onSkipClick) {
                        Text(
                            "Skip for now",
                            color = Color(0xFF45445C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF3EDF7),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF1E1B4B), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF1E1B4B), fontSize = 16.sp)
            }
            content()
        }
    }
}

@Composable
fun DetailTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(label, fontSize = 11.sp, color = Color(0xFF64748B))
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                if (value.isEmpty()) {
                    Text(placeholder, color = Color.LightGray, fontSize = 15.sp)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, color = Color.Black),
                    singleLine = singleLine,
                    minLines = minLines
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactMoreDetailsPreview() {
    AppTheme {
        ContactMoreDetailsScreen(
            uiState = ContactMoreDetailsUiState(
                firstName = "Sarah",
                lastName = "Chen",
                fullName = "Sarah Chen",
                phoneNumber = "+1 (555) 234-5678",
                tag = ProfileType.WORK
            ),
            onBackClick = {},
            onSaveClick = {},
            onSkipClick = {},
            onOrganizationChange = {},
            onRoleChange = {},
            onSocialLinkChange = { _, _ -> },
            onLocationChange = {},
            onNotesChange = {}
        )
    }
}
