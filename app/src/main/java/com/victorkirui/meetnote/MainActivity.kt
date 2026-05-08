package com.victorkirui.meetnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.victorkirui.meetnote.presentation.OnBoardingScreenRoute
import com.victorkirui.meetnote.presentation.ProfileSetupRoute
import com.victorkirui.meetnote.presentation.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

enum class Screen {
    Splash, Onboarding, ProfileSetup
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.Splash) }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            Screen.Splash -> {
                SplashScreen(onTimeout = {
                    currentScreen = Screen.Onboarding
                })
            }
            Screen.Onboarding -> {
                OnBoardingScreenRoute(
                    onGetStartedClick = {
                        currentScreen = Screen.ProfileSetup
                    }
                )
            }
            Screen.ProfileSetup -> {
                ProfileSetupRoute(
                    onSaveProfile = {
                        // TODO: Handle profile save
                    }
                )
            }
        }
    }
}
