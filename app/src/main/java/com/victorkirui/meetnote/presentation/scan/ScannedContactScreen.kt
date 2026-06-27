package com.victorkirui.meetnote.presentation.scan

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.victorkirui.meetnote.presentation.contacts.add.AddContactEvents
import com.victorkirui.meetnote.presentation.contacts.add.ContactSavedBottomSheet
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScannedContactRoute(
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
    onViewContactClick: (Long, String) -> Unit,
    onNavigateToError: () -> Unit,
    viewModel: ScannedContactViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val events by viewModel.events.collectAsState()
    val context = LocalContext.current
    var confirmationSheetData by remember { mutableStateOf<AddContactEvents.ShowConfirmationSheet?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is AddContactEvents.ShowToast -> {
                    Toast.makeText(context, event.errorMessage, Toast.LENGTH_SHORT).show()
                }
                is AddContactEvents.ShowConfirmationSheet -> {
                    confirmationSheetData = event
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                is ScannedContactEffect.NavigateToError -> onNavigateToError()
            }
        }
    }

    ScannedContactScreen(
        uiState = uiState,
        events = events,
        onBackClick = onBackClick,
        onSaveClick = viewModel::saveContact,
        onEventSelected = viewModel::onEventSelected,
        confirmationSheetData = confirmationSheetData,
        onDismissConfirmation = { confirmationSheetData = null },
        onDoneClick = onDoneClick,
        onViewContactClick = onViewContactClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScannedContactScreen(
    uiState: ScannedContactUiState,
    events: List<com.victorkirui.meetnote.domain.model.EventsSummary>,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onEventSelected: (Long, String) -> Unit,
    confirmationSheetData: AddContactEvents.ShowConfirmationSheet?,
    onDismissConfirmation: () -> Unit,
    onDoneClick: () -> Unit,
    onViewContactClick: (Long, String) -> Unit
) {
    var showEventSelector by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Scanned Contact", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Profile Card (Read-only)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = if (uiState.tag == ProfileType.WORK) Color(0xFF1E1B4B) else Color(0xFFB34E3C))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = uiState.fullName.split(" ")
                            .filter { it.isNotEmpty() }
                            .joinToString("") { it.take(1) }
                            .uppercase()
                            .take(2)
                        Text(initials.ifBlank { "U" }, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = uiState.fullName,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    if (!uiState.role.isNullOrBlank() || !uiState.organization.isNullOrBlank()) {
                        val sub = listOfNotNull(uiState.role, uiState.organization).joinToString(" · ")
                        Text(sub, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    }
                }
            }

            // Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7).copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!uiState.email.isNullOrBlank()) {
                        InfoRow(icon = Icons.Default.Email, label = "Email", value = uiState.email)
                    }
                    if (!uiState.phoneNumber.isNullOrBlank()) {
                        InfoRow(icon = Icons.Default.Phone, label = "Phone", value = uiState.phoneNumber)
                    }
                }
            }

            // Event Selection (The only editable field)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F9).copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Where did you meet?", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF64748B))
                    
                    Box {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable { showEventSelector = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.LightGray),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF1E1B4B))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = uiState.metAt.ifEmpty { "Select an event" },
                                    modifier = Modifier.weight(1f),
                                    color = if (uiState.metAt.isEmpty()) Color.Gray else Color.Black
                                )
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray)
                            }
                        }

                        DropdownMenu(
                            expanded = showEventSelector,
                            onDismissRequest = { showEventSelector = false },
                            modifier = Modifier.fillMaxWidth(0.8f).background(Color.White)
                        ) {
                            events.forEach { event ->
                                DropdownMenuItem(
                                    text = { Text(event.eventName) },
                                    onClick = {
                                        onEventSelected(event.eventId, event.eventName)
                                        showEventSelector = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                enabled = uiState.metAt.isNotEmpty()
            ) {
                Text("Save Contact", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    confirmationSheetData?.let { data ->
        ContactSavedBottomSheet(
            onDismissRequest = onDismissConfirmation,
            contactName = "${data.firstName} ${data.lastName}",
            metAt = data.metAt,
            initials = (data.firstName.take(1) + data.lastName.take(1)).uppercase(),
            profilePictureUri = data.profilePictureUri,
            onViewContactClick = { 
                onDismissConfirmation()
                onViewContactClick(data.id, data.tag.name)
            },
            onDoneClick = {
                onDismissConfirmation()
                onDoneClick()
            }
        )
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1E1B4B), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1B4B))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScannedContactPreview() {
    AppTheme {
        ScannedContactScreen(
            uiState = ScannedContactUiState(
                fullName = "Alex Johnson",
                email = "alex.johnson@example.com",
                phoneNumber = "+1 (555) 000-1234",
                organization = "MeetNote Corp",
                role = "Lead Designer",
                tag = ProfileType.WORK,
                metAt = "Tech Summit 2024"
            ),
            events = listOf(
                com.victorkirui.meetnote.domain.model.EventsSummary(
                    eventId = 1,
                    eventName = "Tech Summit 2024",
                    eventDate = "2024-06-25",
                    location = "San Francisco",
                    eventType = "WORK",
                    numberOfContacts = 5
                ),
                com.victorkirui.meetnote.domain.model.EventsSummary(
                    eventId = 2,
                    eventName = "Nairobi Meetup",
                    eventDate = "2024-07-10",
                    location = "Nairobi",
                    eventType = "SOCIAL",
                    numberOfContacts = 2
                )
            ),
            onBackClick = {},
            onSaveClick = {},
            onEventSelected = { _, _ -> },
            confirmationSheetData = null,
            onDismissConfirmation = {},
            onDoneClick = {},
            onViewContactClick = { _, _ -> }
        )
    }
}


