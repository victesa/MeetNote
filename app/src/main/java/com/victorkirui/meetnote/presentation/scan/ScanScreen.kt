package com.victorkirui.meetnote.presentation.scan

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun ScanScreen(
    onBackClick: () -> Unit,
    onScanResult: (String) -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasScanned by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            ScannerView(onScanResult = {
                if (!hasScanned) {
                    android.util.Log.d("DEBUG_SCAN", "Step 1: Scanner detected code: $it")
                    hasScanned = true
                    onScanResult(it)
                }
            })
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Camera permission required", color = Color.White)
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
            }
        }

        // Overlay UI
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
                
                Text(
                    "Scan QR Code",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { /* Toggle Flash */ },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = "Flash", tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                "Align QR code within the frame to scan",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )
        }
    }
}

@Composable
private fun ScannerView(
    onScanResult: (String) -> Unit
) {
    val context = LocalContext.current
    val compoundBarcodeView = remember {
        CompoundBarcodeView(context).apply {
            // Configure the view
            this.setStatusText("") // Remove default status text
            this.decodeContinuous { result ->
                result.text?.let { onScanResult(it) }
            }
        }
    }

    DisposableEffect(Unit) {
        compoundBarcodeView.resume()
        onDispose {
            compoundBarcodeView.pause()
        }
    }

    AndroidView(
        factory = { compoundBarcodeView },
        modifier = Modifier.fillMaxSize()
    )
}
