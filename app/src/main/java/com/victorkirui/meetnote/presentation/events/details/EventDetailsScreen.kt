package com.victorkirui.meetnote.presentation.events.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import coil3.compose.AsyncImage
import com.victorkirui.meetnote.domain.model.ContactMetSummary
import com.victorkirui.meetnote.domain.model.EventDetailsModel
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

@Composable
fun EventDetailsRoute(
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onContactClick: (Long, String) -> Unit,
    viewModel: EventDetailsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is EventDetailsUiEvent.EventDeleted -> onBackClick()
            }
        }
    }

    EventDetailsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onEditClick = { id -> onEditClick(id) },
        onContactClick = onContactClick,
        onDeleteClick = viewModel::deleteEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    uiState: EventDetailsUiState,
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onContactClick: (Long, String) -> Unit,
    onDeleteClick: (Boolean) -> Unit
) {
    val professionalColor = Color(0xFF1E1B4B)
    val socialColor = Color(0xFFB34E3C)

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Event") },
            text = { Text("Do you want to delete all contacts associated with this event as well, or just the event group?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(true)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete Event & Contacts", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(false)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete Event Group Only")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            if (uiState is EventDetailsUiState.Success) {
                val event = uiState.event
                val headerColor = if (event.eventType == ProfileType.WORK.name) professionalColor else socialColor
                TopAppBar(
                    title = { Text("Event", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEditClick(event.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Delete Event", color = Color.Red) },
                                    onClick = {
                                        showMenu = false
                                        showDeleteDialog = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = headerColor)
                )
            }
        }
    ) { padding ->
        when (uiState) {
            is EventDetailsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = professionalColor)
                }
            }
            is EventDetailsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = Color.Red)
                }
            }
            is EventDetailsUiState.Success -> {
                val event = uiState.event
                val headerColor = if (event.eventType == ProfileType.WORK.name) professionalColor else socialColor

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
                            .background(headerColor)
                            .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Icon Replacement (Calendar instead of Profile Pic)
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column {
                                Text(
                                    text = event.name,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val isWork = event.eventType == ProfileType.WORK.name
                                    Icon(
                                        if (isWork) Icons.Default.Work else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isWork) "Work Event" else "Social Event",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(12.dp))
                                    
                                    Icon(
                                        Icons.Default.People,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${event.contactsExchanged}",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.CalendarMonth,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = event.date,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }

                                if (!event.location.isNullOrBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Outlined.LocationOn,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = event.location,
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Contacts Met Section
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(
                            "CONTACTS MET",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (event.contactsMet.isEmpty()) {
                            Text(
                                "No contacts met at this event yet.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        } else {
                            event.contactsMet.forEach { contact ->
                                ContactItem(contact = contact) {
                                    onContactClick(contact.id, contact.tag)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Notes Section
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(
                            "NOTES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Outlined.Notes,
                                        contentDescription = null,
                                        tint = Color(0xFF1E1B4B)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = event.notes ?: "No notes for this event.",
                                    fontSize = 14.sp,
                                    color = Color(0xFF1E293B),
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun ContactItem(
    contact: ContactMetSummary,
    onClick: () -> Unit
) {
    val isWork = contact.tag == ProfileType.WORK.name
    val themeColor = if (isWork) Color(0xFF1E1B4B) else Color(0xFFB34E3C)
    val backgroundColor = themeColor.copy(alpha = 0.1f)

    val initials = remember(contact.fullName) {
        contact.fullName.split(" ")
            .filter { it.isNotEmpty() }
            .joinToString("") { it.take(1) }
            .uppercase()
            .take(2)
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                if (!contact.profilePictureUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = contact.profilePictureUri,
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
                
                // Small indicator icon
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(themeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isWork) Icons.Default.Work else Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
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
                val subtext = buildString {
                    if (!contact.role.isNullOrBlank()) append(contact.role)
                    if (!contact.role.isNullOrBlank() && !contact.organization.isNullOrBlank()) append(" · ")
                    if (!contact.organization.isNullOrBlank()) append(contact.organization)
                }
                if (subtext.isNotEmpty()) {
                    Text(
                        text = subtext,
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        maxLines = 1
                    )
                }
            }

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
private fun EventDetailsPreview() {
    AppTheme {
        EventDetailsScreen(
            uiState = EventDetailsUiState.Success(
                event = EventDetailsModel(
                    id = 1,
                    name = "Nairobi Tech Summit",
                    date = "November 8, 2024",
                    location = "Nairobi, Kenya",
                    eventType = ProfileType.WORK.name,
                    notes = "Annual technology conference bringing together innovators from across East Africa.",
                    contactsExchanged = 4,
                    contactsMet = listOf(
                        ContactMetSummary(1, "Sarah Chen", "Product Manager", "TechCorp", null, ProfileType.WORK.name),
                        ContactMetSummary(2, "James Okafor", "Software Engineer", "StartupXYZ", null, ProfileType.WORK.name),
                        ContactMetSummary(3, "Li Wei", "Data Scientist", "Meta", null, ProfileType.SOCIAL.name),
                        ContactMetSummary(4, "Maria Santos", "Marketing Director", "Agency Co", null, ProfileType.SOCIAL.name)
                    )
                )
            ),
            onBackClick = {},
            onEditClick = {},
            onContactClick = { _, _ -> },
            onDeleteClick = {}
        )
    }
}
