package com.victorkirui.meetnote.presentation.events.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun AddEventRoute(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AddEventViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is AddEventUiEvent.NavigateBack -> onSaveSuccess()
                is AddEventUiEvent.ShowToast -> {
                    // Handle toast if needed, or use a Scaffold snackbar
                }
            }
        }
    }

    AddEventScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onNameChange = viewModel::onNameChange,
        onTypeChange = viewModel::onTypeChange,
        onDateChange = viewModel::onDateChange,
        onLocationChange = viewModel::onLocationChange,
        onNotesChange = viewModel::onNotesChange,
        onSaveClick = viewModel::saveEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    uiState: AddEventUiState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onTypeChange: (ProfileType) -> Unit,
    onDateChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = { 
                    Text(
                        "New Event", 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF1E1B4B),
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back", 
                            tint = Color(0xFF1E1B4B)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onSaveClick, enabled = uiState.canSave) {
                        Text(
                            "Save", 
                            color = if (uiState.canSave) Color(0xFF1E1B4B) else Color.Gray, 
                            fontWeight = FontWeight.Bold
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Event Name
            EventInputField(
                label = "Event Name",
                value = uiState.eventName,
                onValueChange = onNameChange,
                placeholder = "Nairobi Tech Summit",
                leadingIcon = Icons.Outlined.CalendarMonth
            )

            // Event Type
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Event Type", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 14.sp, 
                    color = Color(0xFF64748B)
                )
                Text(
                    "What kind of event is this?", 
                    fontSize = 12.sp, 
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EventTypeCard(
                        label = "Work",
                        icon = Icons.Default.Work,
                        isSelected = uiState.eventType == ProfileType.WORK,
                        onClick = { onTypeChange(ProfileType.WORK) },
                        selectedColor = Color(0xFF1E1B4B),
                        modifier = Modifier.weight(1f)
                    )
                    EventTypeCard(
                        label = "Social",
                        icon = Icons.Default.Person,
                        isSelected = uiState.eventType == ProfileType.SOCIAL,
                        onClick = { onTypeChange(ProfileType.SOCIAL) },
                        selectedColor = Color(0xFFFDF2F0),
                        contentColor = Color(0xFFB34E3C),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Date
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Date", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 14.sp, 
                    color = Color(0xFF64748B)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.CalendarMonth, 
                            contentDescription = null, 
                            tint = Color(0xFF1E1B4B)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState.eventDate.ifEmpty { "November 8, 2024" },
                            modifier = Modifier.weight(1f),
                            color = if (uiState.eventDate.isEmpty()) Color.Gray else Color.Black
                        )
                        Icon(
                            Icons.Default.KeyboardArrowDown, 
                            contentDescription = null, 
                            tint = Color.Gray
                        )
                    }
                    // Border simulation
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(
                            color = Color.LightGray,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                        )
                    }
                }
            }

            // Location
            EventInputField(
                label = "Location",
                value = uiState.location,
                onValueChange = onLocationChange,
                placeholder = "Nairobi, Kenya",
                leadingIcon = Icons.Outlined.LocationOn
            )

            // Notes
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Notes", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 14.sp, 
                    color = Color(0xFF64748B)
                )
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = onNotesChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { 
                        Text(
                            "Annual technology conference bringing together innovators from across East Africa.", 
                            color = Color.Gray,
                            fontSize = 14.sp
                        ) 
                    },
                    leadingIcon = { 
                        Icon(
                            Icons.AutoMirrored.Outlined.Notes, 
                            contentDescription = null, 
                            tint = Color(0xFF1E1B4B)
                        ) 
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF45445C)),
                enabled = uiState.canSave
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Event", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Cancel Button
            TextButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF45445C))
            ) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateChange(date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun EventInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            label, 
            fontWeight = FontWeight.Bold, 
            fontSize = 14.sp, 
            color = Color(0xFF64748B)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = Color(0xFF1E1B4B)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
fun EventTypeCard(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White
) {
    Surface(
        modifier = modifier
            .height(64.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) selectedColor else Color(0xFFF3EDF7)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) contentColor else Color(0xFF45445C)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) contentColor else Color(0xFF45445C)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEventPreview() {
    AppTheme {
        AddEventScreen(
            uiState = AddEventUiState(
                eventName = "Nairobi Tech Summit",
                eventDate = "November 8, 2024",
                location = "Nairobi, Kenya",
                notes = "Annual technology conference bringing together innovators from across East Africa."
            ),
            onBackClick = {},
            onNameChange = {},
            onTypeChange = {},
            onDateChange = {},
            onLocationChange = {},
            onNotesChange = {},
            onSaveClick = {}
        )
    }
}
