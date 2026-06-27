package com.victorkirui.meetnote.presentation.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.victorkirui.meetnote.R
import com.victorkirui.meetnote.domain.model.EventsSummary
import com.victorkirui.meetnote.presentation.contacts.list.ContactsFilterChip
import com.victorkirui.meetnote.presentation.contacts.list.ContactsSearchBar
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EventsScreenRoute(
    onEventClick: (Long) -> Unit,
    onAddEventClick: () -> Unit,
    onHomeClick: () -> Unit,
    onScanClick: () -> Unit,
    onContactsClick: () -> Unit,
    viewModel: EventsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EventsScreen(
        uiState = uiState,
        onEventClick = onEventClick,
        onAddEventClick = onAddEventClick,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onTabSelected = viewModel::onTabSelected,
        onHomeClick = onHomeClick,
        onScanClick = onScanClick,
        onContactsClick = onContactsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventsScreen(
    uiState: EventsUiState,
    onEventClick: (Long) -> Unit,
    onAddEventClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (ProfileType?) -> Unit,
    onHomeClick: () -> Unit,
    onScanClick: () -> Unit,
    onContactsClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        "Events",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B)
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_home),
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Home") }
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
                    onClick = onContactsClick,
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
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_event),
                            contentDescription = "Events",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Events") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1E1B4B),
                        selectedTextColor = Color(0xFF1E1B4B),
                        indicatorColor = Color(0xFF1E1B4B).copy(alpha = 0.1f)
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEventClick,
                containerColor = Color(0xFFF1F1F9),
                contentColor = Color(0xFF1E1B4B),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            ContactsSearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange,
                placeholder = "Search events..."
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContactsFilterChip(
                    label = "All",
                    isSelected = uiState.selectedTab == null,
                    onClick = { onTabSelected(null) },
                    icon = if (uiState.selectedTab == null) Icons.Default.Check else null,
                    selectedColor = Color(0xFF1E1B4B)
                )
                ContactsFilterChip(
                    label = "Work",
                    isSelected = uiState.selectedTab == ProfileType.WORK,
                    onClick = { onTabSelected(ProfileType.WORK) },
                    icon = Icons.Default.Work,
                    selectedColor = Color(0xFF1E1B4B)
                )
                ContactsFilterChip(
                    label = "Social",
                    isSelected = uiState.selectedTab == ProfileType.SOCIAL,
                    onClick = { onTabSelected(ProfileType.SOCIAL) },
                    icon = Icons.Default.Person,
                    selectedColor = Color(0xFFB34E3C)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState.eventsListState) {
                is EventsListState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1E1B4B))
                    }
                }
                is EventsListState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is EventsListState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        state.groupedEvents.forEach { (header, events) ->
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = header,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E1B4B),
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = Color(0xFFF1F1F9),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = events.size.toString(),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                            items(events) { event ->
                                EventItem(event = event, onClick = { onEventClick(event.eventId) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventItem(
    event: EventsSummary,
    onClick: () -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    val date = try {
        LocalDate.parse(event.eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    } catch (_: Exception) {
        LocalDate.now()
    }
    
    val month = date.month.getDisplayName(java.time.format.TextStyle.SHORT, locale).uppercase()
    val day = date.dayOfMonth.toString()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF3EDF7).copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date Card
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (event.eventType == ProfileType.WORK.name) Color(0xFFD0D0E1) else Color(0xFFF5D5C0)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = month,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = day,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.eventName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E1B4B)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.location ?: "Online",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.numberOfContacts} contacts",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFD0D0E1)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventsScreenPreview() {
    val mockEvents = listOf(
        EventsSummary(1, "Nairobi Tech Summit", "2023-11-08", "Nairobi, Kenya", "WORK", 4),
        EventsSummary(2, "Coffee Meetup", "2023-11-02", "Nairobi, Kenya", "SOCIAL", 2),
        EventsSummary(3, "Design Week", "2023-10-28", "Nairobi, Kenya", "WORK", 6),
        EventsSummary(4, "AI Conference", "2023-10-15", "Nairobi, Kenya", "WORK", 3)
    )
    val grouped = mockEvents.groupBy { 
        if (it.eventDate.startsWith("2023-11")) "This Month" else "October"
    }
    
    AppTheme(dynamicColor = false) {
        EventsScreen(
            uiState = EventsUiState(eventsListState = EventsListState.Success(grouped)),
            onEventClick = {},
            onAddEventClick = {},
            onSearchQueryChange = {},
            onTabSelected = {},
            onHomeClick = {},
            onScanClick = {},
            onContactsClick = {}
        )
    }
}
