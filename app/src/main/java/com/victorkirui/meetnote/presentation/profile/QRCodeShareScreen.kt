package com.victorkirui.meetnote.presentation.profile

import android.graphics.BitmapFactory
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.victorkirui.meetnote.domain.model.ProfileDataModel
import com.victorkirui.meetnote.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun QRCodeShareScreenRoute(
    profileType: String,
    onBackClick: () -> Unit,
    viewModel: QRCodeShareScreenViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val profile by if (profileType == "WORK") {
        viewModel.uiWorkProfileState.collectAsState()
    } else {
        viewModel.uiSocialProfileState.collectAsState()
    }

    val qrCodeBytes = remember(profile) {
        profile?.let { viewModel.generateQRCode(it) }
    }

    QRCodeShareScreen(
        profile = profile,
        qrCodeBytes = qrCodeBytes,
        profileType = profileType,
        onBackClick = onBackClick,
        onShareClick = {
            qrCodeBytes?.let { bytes ->
                shareQRCode(context, bytes, profile?.fullName ?: "Contact")
            }
        }
    )
}

@Composable
private fun QRCodeShareScreen(
    profile: ProfileDataModel?,
    qrCodeBytes: ByteArray?,
    profileType: String,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val isWork = profileType == "WORK"
    val themeColor = if (isWork) Color(0xFF1E1B4B) else Color(0xFFB34E3C)
    val backgroundColor = Color(0xFFFAF8FE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Decorative background shapes
        DecorativeBackground(themeColor)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = themeColor
                        )
                    }
                }
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onShareClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Share this QR",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (profile != null) {
                    Box(contentAlignment = Alignment.TopCenter) {
                        // Main Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(top = 40.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = themeColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .padding(top = 40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // QR Code Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(Color.White)
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    qrCodeBytes?.let { bytes ->
                                        val bitmap = remember(bytes) {
                                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                        }
                                        if (bitmap != null) {
                                            Image(
                                                bitmap = bitmap.asImageBitmap(),
                                                contentDescription = "QR Code",
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    } ?: CircularProgressIndicator(modifier = Modifier.size(40.dp), color = themeColor)
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = profile.fullName,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center
                                )

                                val subTitle = if (isWork) {
                                    val role = profile.role.orEmpty()
                                    val org = profile.organization.orEmpty()
                                    if (role.isNotBlank() && org.isNotBlank()) "$role · $org" else role.ifBlank { org }
                                } else {
                                    profile.userName
                                }

                                if (subTitle.isNotEmpty()) {
                                    Text(
                                        text = subTitle,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Overlapping Circle (Profile Pic or Initials)
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = Color(0xFFD0D0E1), // Light blue-gray for the initials circle
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (!profile.profilePicture.isNullOrBlank()) {
                                    AsyncImage(
                                        model = profile.profilePicture,
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    val initials = profile.fullName.split(" ")
                                        .filter { it.isNotEmpty() }
                                        .joinToString("") { it.take(1) }
                                        .uppercase()
                                        .take(2)

                                    Text(
                                        text = initials,
                                        color = themeColor,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                } else {
                    CircularProgressIndicator(color = themeColor)
                }
            }
        }
    }
}

private fun shareQRCode(context: Context, bytes: ByteArray, name: String) {
    try {
        val cachePath = File(context.cacheDir, "shared_images")
        cachePath.mkdirs()
        val file = File(cachePath, "qr_code.png")
        val stream = FileOutputStream(file)
        stream.write(bytes)
        stream.close()

        val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        if (contentUri != null) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, "Scan my QR code to get my contact info on MeetNote!")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
private fun DecorativeBackground(themeColor: Color) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top Right Shape
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-60).dp)
                .clip(RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp, bottomEnd = 100.dp))
                .background(themeColor.copy(alpha = 0.8f))
        )

        // Bottom Left Shape
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 80.dp)
                .clip(CircleShape)
                .background(Color(0xFF94A3B8).copy(alpha = 0.4f))
        )

        // Bottom Right Shape
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(themeColor.copy(alpha = 0.15f))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QRCodeShareScreenPreview() {
    AppTheme {
        QRCodeShareScreen(
            profile = ProfileDataModel(
                fullName = "Alex Johnson",
                role = "Senior Product Designer",
                organization = "Acme Corp",
                profileType = "Work"
            ),
            qrCodeBytes = null,
            profileType = "WORK",
            onBackClick = {},
            onShareClick = {}
        )
    }
}
