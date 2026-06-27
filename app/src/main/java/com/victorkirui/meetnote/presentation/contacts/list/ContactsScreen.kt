package com.victorkirui.meetnote.presentation.contacts.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.victorkirui.meetnote.R
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ContactsScreenRoute(
    onHomeClick: () -> Unit,
    onContactClick: (Long, String) -> Unit,
    onScanClick: () -> Unit,
    onEventClick: () -> Unit,
    onAddContactClick: () -> Unit,
    viewModel: ContactListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTag by viewModel.selectedTag.collectAsStateWithLifecycle()

    ContactsScreen(
        uiState = uiState,
        selectedTag = selectedTag,
        onHomeClick = onHomeClick,
        onContactClick = { id ->
            if (uiState is ContactListState.Success && (uiState as ContactListState.Success).isSelectionMode) {
                viewModel.toggleSelection(id)
            } else {
                val contact = (uiState as? ContactListState.Success)?.contactListUiState?.find { it.id == id }
                val tag = contact?.tag ?: "WORK"
                onContactClick(id, tag)
            }
        },
        onContactLongClick = viewModel::enterSelectionMode,
        onDeleteSelected = viewModel::deleteSelectedContacts,
        onExitSelectionMode = viewModel::exitSelectionMode,
        onScanClick = onScanClick,
        onEventClick = onEventClick,
        onAddContactClick = onAddContactClick,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onTagSelected = viewModel::onTagSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ContactsScreen(
    uiState: ContactListState,
    selectedTag: ProfileType?,
    onHomeClick: () -> Unit = {},
    onContactClick: (Long) -> Unit = {},
    onContactLongClick: (Long) -> Unit = {},
    onDeleteSelected: () -> Unit = {},
    onExitSelectionMode: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onEventClick: () -> Unit = {},
    onAddContactClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onTagSelected: (ProfileType?) -> Unit = {}
) {
    if (uiState is ContactListState.Success && uiState.isSelectionMode) {
        BackHandler {
            onExitSelectionMode()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                if (uiState is ContactListState.Success && uiState.isSelectionMode) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF1E1B4B)
                        ),
                        title = {
                            Text(
                                "${uiState.selectedIds.size} Selected",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onExitSelectionMode) {
                                Icon(Icons.Default.Close, contentDescription = "Exit Selection", tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = onDeleteSelected) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Selected", tint = Color.White)
                            }
                        }
                    )
                } else {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White
                        ),
                        title = {
                            Text(
                                "Contacts",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1B4B)
                            )
                        }
                    )
                }
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
                        selected = true,
                        onClick = { },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_person),
                                contentDescription = "Contacts",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Contacts") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1B4B),
                            selectedTextColor = Color(0xFF1E1B4B),
                            indicatorColor = Color(0xFF1E1B4B).copy(alpha = 0.1f)
                        )
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
                if (uiState !is ContactListState.Success || !uiState.isSelectionMode) {
                    FloatingActionButton(
                        onClick = onAddContactClick,
                        containerColor = Color(0xFF1E1B4B),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Contact",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                when (uiState) {
                    is ContactListState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF1E1B4B))
                        }
                    }
                    is ContactListState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                        }
                    }
                    is ContactListState.Success -> {
                        ContactsSearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = onSearchQueryChange
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ContactsFilterChip(
                                label = "All",
                                isSelected = selectedTag == null,
                                onClick = { onTagSelected(null) },
                                icon = if (selectedTag == null) Icons.Default.Check else null,
                                selectedColor = Color(0xFF1E1B4B)
                            )
                            ContactsFilterChip(
                                label = "Work",
                                isSelected = selectedTag == ProfileType.WORK,
                                onClick = { onTagSelected(ProfileType.WORK) },
                                icon = Icons.Default.Work,
                                selectedColor = Color(0xFF1E1B4B)
                            )
                            ContactsFilterChip(
                                label = "Social",
                                isSelected = selectedTag == ProfileType.SOCIAL,
                                onClick = { onTagSelected(ProfileType.SOCIAL) },
                                icon = Icons.Default.Person,
                                selectedColor = Color(0xFFB34E3C)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (uiState.contactListUiState.isEmpty()) {
                            EmptyContactsState()
                        } else {
                            Text(
                                text = "${uiState.contactListUiState.size} contacts",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            val groupedContacts = uiState.contactListUiState.groupBy { it.fullName.first().uppercaseChar() }
                            val sortedKeys = groupedContacts.keys.sorted()

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 88.dp)
                            ) {
                                sortedKeys.forEach { char ->
                                    item {
                                        Text(
                                            text = char.toString(),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E1B4B),
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                                        )
                                    }
                                    items(groupedContacts[char] ?: emptyList()) { contact ->
                                        val isSelected = uiState.selectedIds.contains(contact.id)
                                        ContactListItem(
                                            contact = contact,
                                            isSelected = isSelected,
                                            isSelectionMode = uiState.isSelectionMode,
                                            onClick = { onContactClick(contact.id) },
                                            onLongClick = { onContactLongClick(contact.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search contacts..."
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            data?.get(0)?.let { onQueryChange(it) }
        }
    }

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp)),
        placeholder = { Text(placeholder, color = Color(0xFF64748B)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
        trailingIcon = {
            IconButton(
                onClick = {
                    try {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search")
                        }
                        voiceLauncher.launch(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Speech recognition not available on this device", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Voice Search", tint = Color(0xFF64748B))
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF1F1F9),
            unfocusedContainerColor = Color(0xFFF1F1F9),
            disabledContainerColor = Color(0xFFF1F1F9),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = true
    )
}

@Composable
fun ContactsFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    selectedColor: Color = Color(0xFF1E1B4B)
) {
    Surface(
        modifier = Modifier
            .height(40.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) selectedColor else Color.White,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isSelected) Color.White else Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFF64748B),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactListItem(
    contact: ContactListUiState,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val isWork = contact.tag?.uppercase() == ProfileType.WORK.name
    val themeColor = if (isWork) Color(0xFF1E1B4B) else Color(0xFFB34E3C)
    val backgroundColor = if (isSelected) themeColor.copy(alpha = 0.15f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(themeColor.copy(alpha = 0.1f)),
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
                    val initials = contact.fullName.split(" ")
                        .filter { it.isNotEmpty() }
                        .joinToString("") { it.take(1) }
                        .uppercase()
                        .take(2)

                    Text(
                        text = initials.ifBlank { contact.fullName.take(1).uppercase() },
                        fontWeight = FontWeight.Bold,
                        color = themeColor,
                        fontSize = 20.sp
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(themeColor)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isWork) Icons.Default.Work else Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.fullName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1E1B4B)
            )
            val subtitle = remember(contact) {
                val components = mutableListOf<String>()
                if (!contact.role.isNullOrBlank()) components.add(contact.role)
                if (!contact.organization.isNullOrBlank()) components.add(contact.organization)
                val infoPart = components.joinToString(" · ")
                
                val metAtPart = if (!contact.metAt.isNullOrBlank()) "Met: ${contact.metAt}" else ""
                
                listOf(infoPart, metAtPart).filter { it.isNotBlank() }.joinToString(" · ")
            }

            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (isSelectionMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(checkedColor = themeColor)
            )
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EmptyContactsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.PersonSearch,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No contacts found",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = "Try a different search or filter",
            fontSize = 14.sp,
            color = Color.LightGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsScreenPreview() {
    val mockContacts = listOf(
        ContactListUiState(1, "James Okafor", null, "Software Engineer", "Startup...", "Coffee Mee..."),
        ContactListUiState(2, "Li Wei", null, "Data Scientist", "Meta", "AI Conference"),
        ContactListUiState(3, "Maria Santos", null, "Marketing Director", "Agency ...", "Brand Sum..."),
        ContactListUiState(4, "Priya Nair", null, "UX Designer", "Google", "Design Week"),
        ContactListUiState(5, "Sarah Chen", null, "Product Manager", "Tec...", "Nairobi Tech S")
    )
    AppTheme(dynamicColor = false) {
        ContactsScreen(
            uiState = ContactListState.Success(mockContacts, ""),
            selectedTag = null,
            onHomeClick = {},
            onScanClick = {},
            onEventClick = {},
            onContactLongClick = {},
            onDeleteSelected = {},
            onExitSelectionMode = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyContactsPreview() {
    AppTheme(dynamicColor = false) {
        ContactsScreen(
            uiState = ContactListState.Success(emptyList(), ""),
            selectedTag = null,
            onHomeClick = {},
            onScanClick = {},
            onEventClick = {},
            onContactLongClick = {},
            onDeleteSelected = {},
            onExitSelectionMode = {}
        )
    }
}
