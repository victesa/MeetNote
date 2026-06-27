package com.victorkirui.meetnote.presentation.contacts.details

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.victorkirui.meetnote.R
import com.victorkirui.meetnote.presentation.components.shimmerEffect
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.presentation.state.SocialLinkState
import com.victorkirui.meetnote.ui.theme.AppTheme
import com.victorkirui.meetnote.ui.theme.ContactThemeColors
import com.victorkirui.meetnote.ui.theme.socialColorScheme
import com.victorkirui.meetnote.ui.theme.workColorScheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileType.toColors(): ContactThemeColors {
    return when (this) {
        ProfileType.WORK -> workColorScheme
        ProfileType.SOCIAL -> socialColorScheme
    }
}

@Composable
fun ContactDetailsRoute(
    profileType: ProfileType = ProfileType.WORK,
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: ContactDetailsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is ContactDetailsUiEvent.NavigateBack -> onBackClick()
                is ContactDetailsUiEvent.ShareContact -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, event.vCard)
                        type = "text/x-vcard"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            }
        }
    }

    ContactDetailsScreen(
        profileType = profileType,
        onBackClick = onBackClick,
        onEditClick = {
            if (uiState is ContactDetailsScreenState.Success) {
                onEditClick((uiState as ContactDetailsScreenState.Success).contactDetails.id)
            }
        },
        onDeleteClick = viewModel::deleteContact,
        onShareClick = viewModel::shareContact,
        uiState = uiState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactDetailsScreen(
    profileType: ProfileType,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    uiState: ContactDetailsScreenState
) {
    val currentColorScheme = when (uiState) {
        is ContactDetailsScreenState.Success -> uiState.contactDetails.profileType.toColors()
        is ContactDetailsScreenState.Loading -> uiState.profileType.toColors()
        is ContactDetailsScreenState.Error -> uiState.profileType.toColors()
    }

    var showMenu by remember { mutableStateOf(false) }

    if (uiState is ContactDetailsScreenState.Error) {
        AlertDialog(
            onDismissRequest = onBackClick,
            confirmButton = {
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = currentColorScheme.accentColor)
                ) {
                    Text("OK", color = Color.White)
                }
            },
            title = {
                Text(
                    text = "System Error",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = uiState.errorMessage,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Contact", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Share Contact") },
                                onClick = {
                                    showMenu = false
                                    onShareClick()
                                },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Contact", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = currentColorScheme.headerColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            when (uiState) {
                is ContactDetailsScreenState.Loading -> {
                    ContactDetailsLoadingContent(colorScheme = currentColorScheme)
                }

                is ContactDetailsScreenState.Success -> {
                    ContactDetailsSuccessContent(
                        details = uiState.contactDetails
                    )
                }

                is ContactDetailsScreenState.Error -> {
                    // While the dialog shows on top, we show the loading skeleton underneath
                    // to provide a better visual transition instead of an empty screen
                    ContactDetailsLoadingContent(colorScheme = currentColorScheme)
                }
            }
        }
    }
}

@Composable
private fun ContactDetailsLoadingContent(colorScheme: ContactThemeColors) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(colorScheme.headerColor)
            .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFE8E1F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null)
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Box(modifier = Modifier.width(150.dp).height(24.dp).shimmerEffect())
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.width(100.dp).height(16.dp).shimmerEffect())
            }
        }
    }

    repeat(3) {
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().shimmerEffect())
        }
    }
}

@Composable
private fun ContactDetailsSuccessContent(
    details: ContactDetailsScreenUiState
) {
    val colors = details.profileType.toColors()
    val context = LocalContext.current

    val onCallClick: () -> Unit = {
        details.phoneNumber?.let { phone ->
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
            }
            context.startActivity(intent)
        }
    }

    val onEmailClick: () -> Unit = {
        details.emailAddress?.let { email ->
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
            }
            context.startActivity(Intent.createChooser(intent, "Send email..."))
        }
    }

    val onMessageClick: () -> Unit = {
        details.phoneNumber?.let { phone ->
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phone")
            }
            context.startActivity(intent)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(colors.headerColor)
            .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFE8E1F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (details.profilePictureUri.isNullOrEmpty()) {
                    val initials = remember(details.fullName) {
                        details.fullName.split(" ")
                            .filter { it.isNotEmpty() }
                            .joinToString("") { it.take(1) }
                            .uppercase()
                    }
                    Text(
                        text = initials,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.headerColor
                    )
                } else {
                    AsyncImage(
                        model = details.profilePictureUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = details.fullName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (!details.role.isNullOrEmpty()) {
                    Text(
                        text = details.role,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                if (!details.organization.isNullOrEmpty()) {
                    Text(
                        text = details.organization,
                        fontSize = 14.sp,
                        color = if (details.profileType == ProfileType.WORK) Color(0xFF93C5FD) else Color(0xFFFFE4E1)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_event),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${details.metAt} . ${details.metOn}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    val hasPhone = !details.phoneNumber.isNullOrEmpty()
    val hasEmail = !details.emailAddress.isNullOrEmpty()

    if (hasPhone || hasEmail) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (hasPhone) {
                ActionCard(
                    icon = R.drawable.ic_phone,
                    label = "Call",
                    backgroundColor = colors.sectionColor,
                    accentColor = colors.accentColor,
                    modifier = Modifier.weight(1f),
                    onClick = onCallClick
                )
            }
            if (hasEmail) {
                ActionCard(
                    icon = R.drawable.ic_email,
                    label = "Email",
                    backgroundColor = colors.sectionColor,
                    accentColor = colors.accentColor,
                    modifier = Modifier.weight(1f),
                    onClick = onEmailClick
                )
            }
            if (hasPhone) {
                ActionCard(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    label = "Message",
                    backgroundColor = colors.sectionColor,
                    accentColor = colors.accentColor,
                    modifier = Modifier.weight(1f),
                    onClick = onMessageClick
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    if (details.phoneNumber != null || details.emailAddress != null) {
        InfoSectionCard(
            modifier = Modifier.padding(horizontal = 24.dp),
            backgroundColor = colors.sectionColor
        ) {
            var needsDivider = false
            details.phoneNumber?.let {
                InfoItem(icon = R.drawable.ic_phone, label = "Phone", value = it, accentColor = colors.accentColor)
                needsDivider = true
            }
            if (needsDivider && details.emailAddress != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Black.copy(alpha = 0.05f))
            }
            details.emailAddress?.let {
                InfoItem(icon = R.drawable.ic_email, label = "Email", value = it, accentColor = colors.accentColor)
            }
        }
    }

    if (details.profileType == ProfileType.WORK) {
        val hasCompany = !details.organization.isNullOrEmpty()
        val hasRole = !details.role.isNullOrEmpty()
        val hasLocation = !details.location.isNullOrEmpty()

        if (hasCompany || hasRole || hasLocation) {
            Spacer(modifier = Modifier.height(24.dp))
            InfoSectionCard(
                modifier = Modifier.padding(horizontal = 24.dp),
                backgroundColor = colors.sectionColor
            ) {
                var needsDivider = false

                if (hasCompany) {
                    InfoItem(icon = R.drawable.ic_business, label = "Company", value = details.organization!!, accentColor = colors.accentColor)
                    needsDivider = true
                }
                if (hasRole) {
                    if (needsDivider) HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Black.copy(alpha = 0.05f))
                    InfoItem(icon = Icons.Default.Work, label = "Role", value = details.role!!, accentColor = colors.accentColor)
                    needsDivider = true
                }
                if (hasLocation) {
                    if (needsDivider) HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Black.copy(alpha = 0.05f))
                    InfoItem(icon = R.drawable.ic_event, label = "Location", value = details.location!!, accentColor = colors.accentColor)
                }
            }
        }
    }

    val visibleSocialLinks = details.socialLinks.filter { it.url.isNotBlank() }

    if (visibleSocialLinks.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        InfoSectionCard(
            modifier = Modifier.padding(horizontal = 24.dp),
            backgroundColor = colors.sectionColor
        ) {
            visibleSocialLinks.forEachIndexed { index, state ->
                SocialItem(icon = R.drawable.ic_link, label = state.platform, value = state.url, accentColor = colors.accentColor)
                if (index < visibleSocialLinks.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Black.copy(alpha = 0.05f))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    InfoSectionCard(
        modifier = Modifier.padding(horizontal = 24.dp),
        backgroundColor = colors.sectionColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notes",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.subTextColor
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = details.notes?.ifBlank { "Empty" } ?: "Empty",
            fontSize = 14.sp,
            color = colors.accentColor,
            lineHeight = 20.sp,
            textAlign = if (details.notes.isNullOrBlank()) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun ActionCard(
    icon: Any,
    label: String,
    backgroundColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (icon) {
                is Int -> Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 12.sp, color = accentColor)
        }
    }
}

@Composable
private fun InfoSectionCard(modifier: Modifier = Modifier, backgroundColor: Color, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun InfoItem(icon: Any, label: String, value: String, accentColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            when (icon) {
                is Int -> Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

@Composable
private fun SocialItem(icon: Int, label: String, value: String, accentColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = accentColor)
        }
        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward),
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactDetailsWorkPreview() {
    val mockUiState = ContactDetailsScreenState.Success(contactDetails = ContactDetailsScreenUiState(
        id = 0,
        fullName = "Victor Kirui",
        emailAddress = "victorkirui.dev@gmail.com",
        phoneNumber = "0707413953",
        organization = "Kenya Space Agency",
        role = "Android Engineer",
        location = "Nairobi, Kenya",
        socialLinks = listOf(SocialLinkState(platform = "LinkedIn", url = "victesa")),
        metAt = "Latitude59",
        metOn = "Nov 8",
        notes = null,
        profilePictureUri = null,
        profileType = ProfileType.WORK,
        userName = "VK",
    ))
    AppTheme(dynamicColor = false) {
        ContactDetailsScreen(
            profileType = ProfileType.WORK,
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onShareClick = {},
            uiState = mockUiState
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactDetailsSocialPreview() {
    val mockUiState = ContactDetailsScreenState.Loading(ProfileType.SOCIAL)
    AppTheme(dynamicColor = false) {
        ContactDetailsScreen(
            profileType = ProfileType.SOCIAL,
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onShareClick = {},
            uiState = mockUiState
        )
    }
}
