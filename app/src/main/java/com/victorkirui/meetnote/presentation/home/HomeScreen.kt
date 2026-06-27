package com.victorkirui.meetnote.presentation.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.victorkirui.meetnote.R
import com.victorkirui.meetnote.presentation.home.state.ContactSummaryUiState
import com.victorkirui.meetnote.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.victorkirui.meetnote.presentation.components.shimmerEffect
import com.victorkirui.meetnote.presentation.home.state.ContactsState
import com.victorkirui.meetnote.presentation.home.state.EventsState
import com.victorkirui.meetnote.presentation.home.state.ProfileState
import com.victorkirui.meetnote.presentation.home.state.ProfileUiState
import com.victorkirui.meetnote.presentation.state.ProfileType
import kotlin.math.abs

@Composable
fun HomeScreenRoute(
    onContactClick: () -> Unit,
    onEventClick: () -> Unit,
    onScanClick: () -> Unit,
    onQRCodeShareScreenButtonClicked: (String) -> Unit,
    onAddContactButtonClicked: () -> Unit,
    onProfileClick: () -> Unit,
    onSetupProfileClick: (ProfileType) -> Unit,
    onSeeAllContactsClick: () -> Unit,
    onSeeAllEventsClick: () -> Unit,
    viewModel: HomeScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        profileState = uiState.profileState,
        eventsUiState = uiState.eventsState,
        contactState = uiState.contactsState,
        onContactClick = onContactClick,
        onEventClick = onEventClick,
        onScanClick = onScanClick,
        onQRCodeShareScreenButtonClicked = { onQRCodeShareScreenButtonClicked(it) },
        onAddContactButtonClicked = onAddContactButtonClicked,
        onProfileClick = onProfileClick,
        onSetupProfileClick = onSetupProfileClick,
        onSeeAllContactsClick = onSeeAllContactsClick,
        onSeeAllEventsClick = onSeeAllEventsClick,
        onTabSwitched = viewModel::onTabSwitched,
        activeProfileTab = uiState.activeProfileTab
    )
}

@Composable
private fun HomeScreen(
    profileState: ProfileState,
    eventsUiState: EventsState,
    contactState: ContactsState,
    onContactClick: () -> Unit,
    onEventClick: () -> Unit,
    onScanClick: () -> Unit,
    onQRCodeShareScreenButtonClicked: (String) -> Unit,
    onAddContactButtonClicked: () -> Unit,
    onProfileClick: () -> Unit,
    onSetupProfileClick: (ProfileType) -> Unit,
    onSeeAllContactsClick: () -> Unit,
    onSeeAllEventsClick: () -> Unit,
    onTabSwitched: (ProfileType) -> Unit,
    activeProfileTab: ProfileType
) {

    val professionalColor = Color(0xFF1E1B4B)
    val socialColor = Color(0xFFB34E3C)
    val themeColor = if (activeProfileTab == ProfileType.WORK) professionalColor else socialColor
    val backgroundColor = Color(0xFFF8FAFC)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_home),
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = professionalColor,
                        selectedTextColor = professionalColor,
                        indicatorColor = professionalColor.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onScanClick,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_scan),
                            contentDescription = "Scan",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Scan") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onContactClick,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_person),
                            contentDescription = "Contacts",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Contacts") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onEventClick,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_event),
                            contentDescription = "Events",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Events") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onScanClick,
                containerColor = Color(0xFFE2E8F0),
                contentColor = Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = "Quick Scan",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            HomeTopHeader(
                profileUiState = profileState,
                onProfileClick = onProfileClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                ProfileCard(
                    isWork = activeProfileTab == ProfileType.WORK,
                    themeColor = themeColor,
                    onShareClick = { onQRCodeShareScreenButtonClicked(activeProfileTab.name) },
                    onProfileClick = onProfileClick,
                    onSetupProfileClick = { onSetupProfileClick(activeProfileTab) },
                    profileState = profileState
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileTabSwitcher(
                isWorkSelected = activeProfileTab == ProfileType.WORK,
                onTabSelected = onTabSwitched
            )

            Spacer(modifier = Modifier.height(32.dp))


            RecentEventsSection(
                events = eventsUiState,
                onSeeAllClick = onSeeAllEventsClick
            )

            Spacer(modifier = Modifier.height(32.dp))


            RecentlyMetSection(
                contacts = contactState,
                onContactClick = onContactClick,
                onSeeAllClick = onSeeAllContactsClick
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HomeTopHeader(
    modifier: Modifier = Modifier,
    profileUiState: ProfileState,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "MeetNote",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E1B4B)
        )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF64748B).copy(alpha = 0.2f))
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {

                when(profileUiState){
                    is ProfileState.Loading -> {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile Picture placeholder",
                            modifier = Modifier
                                .fillMaxSize()
                                .shimmerEffect())
                    }

                    is ProfileState.Success->{
                        val state = profileUiState.profileUiState
                        if (!state.profilePictureUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = if (state.profilePictureUri.startsWith("file://")) state.profilePictureUri else "file://${state.profilePictureUri}",
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val initials = state.fullName.split(" ")
                                .filter { it.isNotEmpty() }
                                .joinToString("") { it.take(1) }
                                .uppercase()

                            Text(
                                text = initials.ifBlank { "UN" },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1B4B)
                            )
                        }
                    }

                    is ProfileState.Error->{
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile Picture placeholder",
                            modifier = Modifier.fillMaxSize(), tint = Color.DarkGray)
                    }
                }

            }
    }
}

@Composable
private fun ProfileTabSwitcher(
    isWorkSelected: Boolean,
    onTabSelected: (ProfileType) -> Unit,
    modifier: Modifier = Modifier
) {
    val professionalColor = Color(0xFF1E1B4B)
    val socialColor = Color(0xFFB34E3C)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TabButton(
            text = "Work",
            isSelected = isWorkSelected,
            icon = R.drawable.ic_event,
            activeColor = professionalColor,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(ProfileType.WORK) }
        )
        TabButton(
            text = "Social",
            isSelected = !isWorkSelected,
            icon = R.drawable.ic_person,
            activeColor = socialColor,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(ProfileType.SOCIAL) }
        )
    }
}

@Composable
private fun ProfileCard(
    themeColor: Color,
    onShareClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSetupProfileClick: () -> Unit,
    isWork: Boolean,
    modifier: Modifier = Modifier,
    profileState: ProfileState
) {
    when (profileState) {
        is ProfileState.Loading -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .shimmerEffect(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = themeColor.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {}
        }

        is ProfileState.Success -> {
            val profile = profileState.profileUiState
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = themeColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                // Fix: Dynamically swap icon based on tab selection state
                                painter = painterResource(if (isWork) R.drawable.ic_event else R.drawable.ic_person),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isWork) "Work" else "Social",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = profile.fullName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Clean template string handling null/empty variations safely
                        val subText = if (isWork) {
                            val role = profile.role.orEmpty()
                            val org = profile.organization.orEmpty()
                            if (role.isNotBlank() && org.isNotBlank()) "$role · $org" else role.ifBlank { org }
                        } else {
                            profile.userNameFromFullName
                        }

                        Text(
                            text = subText,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // QR Code Ghost Background
                    Icon(
                        painter = painterResource(R.drawable.ic_qr),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 20.dp, y = (-10).dp)
                    )

                    // Share Button
                    Button(
                        onClick = onShareClick,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_scan),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        is ProfileState.Error -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { onSetupProfileClick() },
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = themeColor.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, themeColor.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(if (isWork) R.drawable.ic_event else R.drawable.ic_person),
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isWork) "Work Profile Not Setup" else "Social Profile Not Setup",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap to setup your profile and start sharing.",
                            fontSize = 12.sp,
                            color = themeColor.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    icon: Int,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) activeColor else activeColor.copy(alpha = 0.1f)
        ),
        elevation = if (isSelected) ButtonDefaults.buttonElevation(defaultElevation = 4.dp) else null
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isSelected) Color.White else activeColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else activeColor
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Text(
            text = "See all",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}

@Composable
private fun EventCard(date: String, name: String, count: String) {
    val tintedBackgroundColor = Color(0xFFF1F5F9) // Subtle bluish-grey tint
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(140.dp), // Fixed height for consistency
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = tintedBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat look like the image
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = date, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    maxLines = 2,
                    lineHeight = 18.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_person), null, modifier = Modifier.size(12.dp), tint = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "$count connections", fontSize = 11.sp, color = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
private fun RecentEventsSection(
    modifier: Modifier = Modifier,
    onSeeAllClick: () -> Unit = {},
    events: EventsState
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            SectionHeader(title = "Recent Events", onSeeAllClick = onSeeAllClick)
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (events) {
            is EventsState.Loading -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Render 2 clean placeholder shimmer boxes matching the exact size of an EventCard
                    Box(modifier = Modifier
                        .width(160.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .shimmerEffect())
                    Box(modifier = Modifier
                        .width(160.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .shimmerEffect())
                }
            }

            is EventsState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(140.dp)
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()

                            // Change the intervals to adjust the dot/dash size and spacing
                            // floatArrayOf(dashLength, dashSpacing)
                            val dashEffect = PathEffect.dashPathEffect(
                                intervals = floatArrayOf(10f, 10f),
                                phase = 0f
                            )

                            drawRoundRect(
                                color = Color(0xFF64748B),
                                style = Stroke(
                                    width = strokeWidth,
                                    pathEffect = dashEffect
                                )
                            )
                        }
                        .background(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = events.message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            is EventsState.Success -> {
                if(events.eventsUiState.isEmpty()){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(140.dp)
                            .background(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFF1F5F9)
                            )
                            .drawBehind {
                                val strokeWidth = 2.dp.toPx()

                                // Change the intervals to adjust the dot/dash size and spacing
                                // floatArrayOf(dashLength, dashSpacing)
                                val dashEffect = PathEffect.dashPathEffect(
                                    intervals = floatArrayOf(10f, 10f),
                                    phase = 0f
                                )

                                drawRoundRect(
                                    color = Color(0xFF64748B),
                                    style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Events Yet",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }else{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.width(8.dp)) // Initial peek spacing

                        events.eventsUiState.take(4).forEach { eventData ->
                            EventCard(
                                date = eventData.eventDate, // Assume fields exist on your domain UI model
                                name = eventData.eventName,
                                count = eventData.numberOfConnections
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp)) // End peek spacing
                    }
                }

            }
        }
    }
}

private val AvatarColorsList = listOf(
    Color(0xFF6366F1),
    Color(0xFF38BDF8),
    Color(0xFF10B981),
    Color(0xFFF59E0B),
    Color(0xFFD97706)
)

@Composable
private fun ContactItem(
    contact: ContactSummaryUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isWork = contact.tag?.uppercase() == ProfileType.WORK.name
    val themeColor = if (isWork) Color(0xFF1E1B4B) else Color(0xFFB34E3C)
    val backgroundColor = themeColor.copy(alpha = 0.1f)

    val initials = remember(contact.fullName) {
        contact.fullName.split(" ")
            .filter { it.isNotEmpty() }
            .joinToString("") { it.take(1) }
            .uppercase()
            .take(2)
    }

    val tintedCardColor = Color(0xFFF5F6FA) //

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = tintedCardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Match the flat design
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                if (!contact.profilePicture.isNullOrEmpty()) {
                    AsyncImage(
                        model = contact.profilePicture,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = initials,
                        color = themeColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                if (!contact.metAt.isNullOrBlank()) {
                    Text(
                        text = "Met: ${contact.metAt}",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        maxLines = 1
                    )
                }
            }

            Text(
                text = contact.timeAgo,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun RecentlyMetSection(
    contacts: ContactsState,
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSeeAllClick: () -> Unit = {}
) {
    when(contacts){
        is ContactsState.Loading->{
            Column(modifier = modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    SectionHeader(title = "Recently Met", onSeeAllClick = onSeeAllClick)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(140.dp)
                        .background(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        )
                        .shimmerEffect()
                ) {

                }
            }
        }

        is ContactsState.Success->{
            Column(modifier = modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    SectionHeader(title = "Recently Met", onSeeAllClick = onSeeAllClick)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    if (contacts.contactsUiState.isNotEmpty()) {
                        contacts.contactsUiState.take(4).forEach { contact ->
                            ContactItem(contact, onClick = onContactClick)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    } else {
                        Text(
                            text = "No recent contacts",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            textAlign = TextAlign.Center,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        is ContactsState.Error->{
            Column(modifier = modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    SectionHeader(title = "Recently Met", onSeeAllClick = onSeeAllClick)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()

                            // Change the intervals to adjust the dot/dash size and spacing
                            // floatArrayOf(dashLength, dashSpacing)
                            val dashEffect = PathEffect.dashPathEffect(
                                intervals = floatArrayOf(10f, 10f),
                                phase = 0f
                            )

                            drawRoundRect(
                                color = Color(0xFF64748B),
                                style = Stroke(
                                    width = strokeWidth,
                                    pathEffect = dashEffect
                                )
                            )
                        }
                        .background(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        )
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = contacts.message,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        textAlign = TextAlign.Center,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }

}


@Preview(showBackground = true, name = "Social Profile View")
@Composable
private fun HomeScreenSocialPreview() {
    val fakeContacts = listOf(
        ContactSummaryUiState(fullName = "Alex Chen", metAt = "DroidconKe", timeAgo = "2 weeks ago", profilePicture = "", id = 1, tag = "SOCIAL")
    )


    AppTheme(dynamicColor = false) {
        HomeScreen(
            profileState = ProfileState.Success(profileUiState = ProfileUiState(
                fullName = "Alex Johnson",
                organization = null,
                role = null,
                userNameFromFullName = "AJ",
                profilePictureUri = null
            )),
            eventsUiState = EventsState.Loading,
            contactState = ContactsState.Loading,
            activeProfileTab = ProfileType.SOCIAL,
            onContactClick = {},
            onEventClick = {},
            onScanClick = {},
            onQRCodeShareScreenButtonClicked = {},
            onAddContactButtonClicked = {},
            onProfileClick = {},
            onSetupProfileClick = {},
            onSeeAllContactsClick = {},
            onSeeAllEventsClick = {},
            onTabSwitched = {}
        )
    }
}

@Preview(showBackground = true, name = "Home View")
@Composable
private fun HomeScreenPreview() {
    val fakeContacts = listOf(
        ContactSummaryUiState(fullName = "Alex Chen", metAt = "DroidconKe", timeAgo = "2 weeks ago", profilePicture = "", id = 1, tag = "WORK")
    )
    AppTheme(dynamicColor = false) {
        HomeScreen(
            profileState = ProfileState.Success(profileUiState = ProfileUiState(
                fullName = "Alex Johnson",
                organization = "Kenya Space Agency",
                role = "Android Engineer",
                userNameFromFullName = "",
                profilePictureUri = null
            )),
            eventsUiState = EventsState.Loading,
            contactState = ContactsState.Success(emptyList()),
            activeProfileTab = ProfileType.WORK,
            onContactClick = {},
            onEventClick = {},
            onScanClick = {},
            onQRCodeShareScreenButtonClicked = {},
            onAddContactButtonClicked = {},
            onProfileClick = {},
            onSetupProfileClick = {},
            onSeeAllContactsClick = {},
            onSeeAllEventsClick = {},
            onTabSwitched = {}
        )
    }
}
