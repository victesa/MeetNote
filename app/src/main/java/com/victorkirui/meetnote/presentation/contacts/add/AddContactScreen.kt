package com.victorkirui.meetnote.presentation.contacts.add

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun AddContactRoute(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    onAddMoreDetailsClick: (Long) -> Unit,
    onViewContactClick: (Long, String) -> Unit,
    viewModel: AddContactViewModel = koinViewModel()
) {
    val formInput by viewModel.formInput.collectAsState()
    val formSubmittingState by viewModel.formSubmittingState.collectAsState()
    val profilePicture by viewModel.profilePictureUri.collectAsState()
    val profilePictureState by viewModel.profilePictureUploadState.collectAsState()
    val events by viewModel.events.collectAsState()
    val context = LocalContext.current

    var confirmationSheetData by remember { mutableStateOf<AddContactEvents.ShowConfirmationSheet?>(null) }

    LaunchedEffect(viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is AddContactEvents.ShowToast -> {
                    Toast.makeText(context, event.errorMessage, Toast.LENGTH_SHORT).show()
                }
                is AddContactEvents.ShowConfirmationSheet -> {
                    confirmationSheetData = event
                }
                is AddContactEvents.NavigateToAddMoreDetailsScreen -> {
                    onAddMoreDetailsClick(event.id)
                }
            }
        }
    }

    AddContactScreen(
        formUiState = formInput,
        formSubmissionState = formSubmittingState,
        profilePictureUri = profilePicture,
        profilePictureUploadState = profilePictureState,
        events = events,
        onBackClick = onBackClick,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onContactMethodChange = viewModel::onContactMethodChange,
        onTagChange = viewModel::onTagChange,
        onMetAtChange = viewModel::onMetAtChange,
        onEventSelected = viewModel::onEventSelected,
        onSaveClick = { viewModel.saveContact(SaveOption.SAVEONLY) },
        onAddMoreDetailsClick = { viewModel.saveContact(SaveOption.SAVEANDADDMOREDETAILS) },
        onPhotoSelected = viewModel::onProfilePictureSelected,
        onDoneClick = onSaveSuccess,
        onViewContactClick = onViewContactClick,
        confirmationSheetData = confirmationSheetData,
        onDismissConfirmation = { confirmationSheetData = null }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    formUiState: AddContactUiState,
    formSubmissionState: AddContactFormSubmissionState,
    profilePictureUri: String?,
    profilePictureUploadState: AddContactFormSubmissionState,
    events: List<com.victorkirui.meetnote.domain.model.EventsSummary>,
    onBackClick: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onContactMethodChange: (ContactMethod) -> Unit,
    onTagChange: (ProfileType) -> Unit,
    onMetAtChange: (String) -> Unit,
    onEventSelected: (Long, String) -> Unit,
    onSaveClick: () -> Unit,
    onAddMoreDetailsClick: () -> Unit,
    onPhotoSelected: (String) -> Unit,
    onDoneClick: () -> Unit,
    onViewContactClick: (Long, String) -> Unit,
    confirmationSheetData: AddContactEvents.ShowConfirmationSheet?,
    onDismissConfirmation: () -> Unit
) {
    var showPhotoSourceSheet by remember { mutableStateOf(false) }
    var showEventSelector by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Launchers for Photo Selection
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onPhotoSelected(it.toString()) }
        showPhotoSourceSheet = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val file = File(context.cacheDir, "temp_profile_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                it.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            onPhotoSelected(Uri.fromFile(file).toString())
        }
        showPhotoSourceSheet = false
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = { Text("New Contact", fontWeight = FontWeight.Bold, color = Color(0xFF1E1B4B)) },
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Profile Photo Section
                ProfilePhotoSection(
                    profilePictureUri = profilePictureUri,
                    uploadState = profilePictureUploadState,
                    onAddPhotoClick = { showPhotoSourceSheet = true }
                )

                // Name Section
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Name", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF64748B))
                    CustomOutlinedTextField(
                        value = formUiState.firstName,
                        onValueChange = onFirstNameChange,
                        placeholder = "First Name",
                        leadingIcon = Icons.Outlined.Person
                    )
                    CustomOutlinedTextField(
                        value = formUiState.lastName,
                        onValueChange = onLastNameChange,
                        placeholder = "Last Name",
                        leadingIcon = Icons.Outlined.Person
                    )
                }

                // Contact Method Section
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Contact Method", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF64748B))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ContactMethodChip(
                            label = "Phone",
                            icon = Icons.Default.Phone,
                            isSelected = formUiState.selectedContactMethod == ContactMethod.PHONE,
                            onClick = { onContactMethodChange(ContactMethod.PHONE) }
                        )
                        ContactMethodChip(
                            label = "Email",
                            icon = Icons.Default.Email,
                            isSelected = formUiState.selectedContactMethod == ContactMethod.EMAIL,
                            onClick = { onContactMethodChange(ContactMethod.EMAIL) }
                        )
                    }

                    CustomOutlinedTextField(
                        value = if (formUiState.selectedContactMethod == ContactMethod.PHONE) formUiState.phoneNumber else formUiState.email,
                        onValueChange = { if (formUiState.selectedContactMethod == ContactMethod.PHONE) onPhoneNumberChange(it) else onEmailChange(it) },
                        placeholder = if (formUiState.selectedContactMethod == ContactMethod.PHONE) "+1 (555) 234-5678" else "example@email.com",
                        leadingIcon = if (formUiState.selectedContactMethod == ContactMethod.PHONE) Icons.Default.Phone else Icons.Default.Email
                    )
                }

                // Meeting Context Section
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Meeting Context", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(8.dp))
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
                                    text = formUiState.metAt.ifEmpty { "Select an event" },
                                    modifier = Modifier.weight(1f),
                                    color = if (formUiState.metAt.isEmpty()) Color.Gray else Color.Black
                                )
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray)
                            }
                        }

                        DropdownMenu(
                            expanded = showEventSelector,
                            onDismissRequest = { showEventSelector = false },
                            modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)
                        ) {
                            if (events.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No events found. Type manually in details.") },
                                    onClick = { showEventSelector = false }
                                )
                            }
                            events.forEach { event ->
                                DropdownMenuItem(
                                    text = { Text(event.eventName) },
                                    onClick = {
                                        onEventSelected(event.eventId, event.eventName)
                                        showEventSelector = false
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Add new event...") },
                                onClick = {
                                    // TODO: Navigate to create event
                                    showEventSelector = false
                                }
                            )
                        }
                    }
                    Text("Tap to choose from your events or add a new one.", fontSize = 12.sp, color = Color(0xFF64748B))
                }

                // Tag Section
                androidx.compose.animation.AnimatedVisibility(visible = formUiState.selectedEventId == null) {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Tag", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF64748B))
                        Text("How do you know this person?", fontSize = 12.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            TagCard(
                                label = "Work",
                                icon = Icons.Default.Work,
                                isSelected = formUiState.selectedTag == ProfileType.WORK,
                                onClick = { onTagChange(ProfileType.WORK) },
                                selectedColor = Color(0xFF0D1B4B),
                                modifier = Modifier.weight(1f)
                            )
                            TagCard(
                                label = "Social",
                                icon = Icons.Default.Person,
                                isSelected = formUiState.selectedTag == ProfileType.SOCIAL,
                                onClick = { onTagChange(ProfileType.SOCIAL) },
                                selectedColor = Color(0xFFFDF2F0),
                                contentColor = Color(0xFFB34E3C),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF45445C)),
                        enabled = formUiState.canSaveContact && formSubmissionState !is AddContactFormSubmissionState.Saving
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Contact", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable { onAddMoreDetailsClick() },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF3EDF7)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Add more details", color = Color(0xFF45445C), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF45445C), modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Saving Dialog Overlay
            if (formSubmissionState is AddContactFormSubmissionState.Saving) {
                AlertDialog(
                    onDismissRequest = { },
                    confirmButton = {},
                    title = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = Color(0xFF45445C))
                            Text(
                                text = "Saving contact...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E1B4B)
                            )
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    containerColor = Color.White
                )
            }
        }
    }

    // Photo Source Picker Sheet
    if (showPhotoSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoSourceSheet = false },
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Select Photo Source",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E1B4B),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                ListItem(
                    headlineContent = { Text("Take Photo", fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFF45445C)) },
                    modifier = Modifier.clickable { cameraLauncher.launch() }
                )
                ListItem(
                    headlineContent = { Text("Choose from Gallery", fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF45445C)) },
                    modifier = Modifier.clickable { 
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
                    }
                )
            }
        }
    }

    // Success Bottom Sheet
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
fun ProfilePhotoSection(
    profilePictureUri: String?,
    uploadState: AddContactFormSubmissionState,
    onAddPhotoClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E1E9))
                    .clickable { onAddPhotoClick() },
                contentAlignment = Alignment.Center
            ) {
                when (uploadState) {
                    is AddContactFormSubmissionState.Saving -> {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp), color = Color(0xFF45445C))
                    }
                    is AddContactFormSubmissionState.Success -> {
                        AsyncImage(
                            model = profilePictureUri,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                    is AddContactFormSubmissionState.Error -> {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Red
                        )
                    }
                    else -> {
                        if (!profilePictureUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = profilePictureUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF45445C))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Add Photo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (uploadState) {
                is AddContactFormSubmissionState.Error -> "Upload failed"
                is AddContactFormSubmissionState.Saving -> "Uploading..."
                is AddContactFormSubmissionState.Success -> "Change photo"
                else -> if (!profilePictureUri.isNullOrEmpty()) "Change photo" else "Add photo"
            },
            fontSize = 14.sp,
            color = if (uploadState is AddContactFormSubmissionState.Error) Color.Red else Color(0xFF64748B)
        )
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        placeholder = { Text(placeholder, color = Color.Gray) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = Color(0xFF1E1B4B)) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.LightGray,
            unfocusedIndicatorColor = Color.LightGray
        )
    )
}

@Composable
fun ContactMethodChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(40.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF45445C) else Color(0xFFF3EDF7)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (isSelected) Color.White else Color(0xFF45445C)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFF45445C),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TagCard(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    contentColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(64.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) selectedColor else Color(0xFFF3EDF7),
        border = if (!isSelected) null else BorderStroke(1.dp, selectedColor)
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
private fun AddContactPreview() {
    AppTheme {
        AddContactScreen(
            onBackClick = {},
            onFirstNameChange = {},
            onLastNameChange = {},
            onEmailChange = {},
            onPhoneNumberChange = {},
            onContactMethodChange = {},
            onTagChange = {},
            onSaveClick = {},
            onAddMoreDetailsClick = {},
            formUiState = AddContactUiState(),
            formSubmissionState = AddContactFormSubmissionState.Idle,
            profilePictureUri = null,
            profilePictureUploadState = AddContactFormSubmissionState.Idle,
            events = emptyList(),
            onMetAtChange = {},
            onEventSelected = { _, _ -> },
            onPhotoSelected = {},
            onDoneClick = {},
            onViewContactClick = {_,_ ->},
            confirmationSheetData = null,
            onDismissConfirmation = {}
        )
    }
}
