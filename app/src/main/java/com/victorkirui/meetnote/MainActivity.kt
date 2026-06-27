package com.victorkirui.meetnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.victorkirui.meetnote.domain.util.qr.ScanResultManager
import com.victorkirui.meetnote.presentation.account.AccountRoute
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.presentation.contacts.add.AddContactRoute
import com.victorkirui.meetnote.presentation.contacts.add.AddContactViewModel
import com.victorkirui.meetnote.presentation.contacts.more_details.ContactMoreDetailsRoute
import com.victorkirui.meetnote.presentation.contacts.more_details.ContactMoreDetailsViewModel
import com.victorkirui.meetnote.presentation.contacts.details.ContactDetailsRoute
import com.victorkirui.meetnote.presentation.contacts.list.ContactsScreenRoute
import com.victorkirui.meetnote.presentation.events.EventsScreenRoute
import com.victorkirui.meetnote.presentation.events.add.AddEventRoute
import com.victorkirui.meetnote.presentation.events.details.EventDetailsRoute
import com.victorkirui.meetnote.presentation.home.HomeScreenRoute
import com.victorkirui.meetnote.presentation.onboarding.OnBoardingScreen2Route
import com.victorkirui.meetnote.presentation.onboarding.OnBoardingScreenRoute
import com.victorkirui.meetnote.presentation.navigation.Screen
import com.victorkirui.meetnote.presentation.profile.ProfileSetupRoute
import com.victorkirui.meetnote.presentation.profile.ProfileSetupSocialRoute
import com.victorkirui.meetnote.presentation.profile.QRCodeShareScreenRoute
import com.victorkirui.meetnote.presentation.scan.ScanErrorScreen
import com.victorkirui.meetnote.presentation.scan.ScanScreen
import com.victorkirui.meetnote.presentation.scan.ScannedContactRoute
import com.victorkirui.meetnote.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.appState.value is AppState.Loading
        }

        enableEdgeToEdge()

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            AppTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MainViewModel = koinViewModel()) {
    val appState by viewModel.appState.collectAsState()
    val navController = rememberNavController()

    if (appState is AppState.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = remember {
        if (appState is AppState.FirstTimeUser) Screen.Onboarding.route else Screen.Home.route
    }

    val onBack: () -> Unit = {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnBoardingScreenRoute(
                onNextClick = { navController.navigate(Screen.Onboarding2.route) }
            )
        }

        composable(Screen.Onboarding2.route) {
            OnBoardingScreen2Route(
                onNextClick = {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.ProfileSetup.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onBackClick = onBack
            )
        }

        composable(Screen.Home.route) {
            HomeScreenRoute(
                onContactClick = { navController.navigate(Screen.ContactList.route) },
                onEventClick = { navController.navigate(Screen.Events.route) },
                onScanClick = { navController.navigate(Screen.Scan.route) },
                onAddContactButtonClicked = { navController.navigate(Screen.AddContact.route) },
                onQRCodeShareScreenButtonClicked = { type ->
                    navController.navigate(Screen.QRCodeShare.createRoute(type))
                },
                onProfileClick = { navController.navigate(Screen.Account.route) },
                onSetupProfileClick = { type ->
                    val destination = if (type == ProfileType.WORK) Screen.ProfileSetup.route else Screen.SocialProfileSetup.route
                    navController.navigate(destination)
                },
                onSeeAllContactsClick = { navController.navigate(Screen.ContactList.route) },
                onSeeAllEventsClick = { navController.navigate(Screen.Events.route) }
            )
        }

        composable(Screen.ContactList.route) {
            ContactsScreenRoute(
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onContactClick = { id, tag ->
                    navController.navigate(Screen.ContactDetails.createRoute(id, tag))
                },
                onScanClick = { navController.navigate(Screen.Scan.route) },
                onEventClick = { navController.navigate(Screen.Events.route) },
                onAddContactClick = { navController.navigate(Screen.AddContact.route) }
            )
        }

        composable(Screen.Events.route) {
            EventsScreenRoute(
                onEventClick = { id -> 
                    navController.navigate(Screen.EventDetails.createRoute(id))
                },
                onAddEventClick = { navController.navigate(Screen.AddEvent.createRoute()) },
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onScanClick = { navController.navigate(Screen.Scan.route) },
                onContactsClick = { navController.navigate(Screen.ContactList.route) }
            )
        }

        composable(
            route = Screen.AddEvent.route,
            arguments = listOf(
                navArgument("event_id") { 
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            AddEventRoute(
                onBackClick = onBack,
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EventDetails.route,
            arguments = listOf(
                navArgument("event_id") { type = NavType.LongType }
            )
        ) {
            EventDetailsRoute(
                onBackClick = onBack,
                onEditClick = { id -> 
                    navController.navigate(Screen.AddEvent.createRoute(id))
                },
                onContactClick = { id, tag ->
                    navController.navigate(Screen.ContactDetails.createRoute(id, tag))
                }
            )
        }

        composable(Screen.AddContact.route) {
            val addContactViewModel: AddContactViewModel = koinViewModel()
            AddContactRoute(
                onBackClick = onBack,
                onSaveSuccess = { navController.popBackStack() },
                onAddMoreDetailsClick = { id ->
                    navController.navigate(Screen.ContactMoreDetails.createRoute(id))
                },
                onViewContactClick = { id, tag ->
                    navController.navigate(Screen.ContactDetails.createRoute(id, tag))
                },
                viewModel = addContactViewModel
            )
        }

        composable(
            route = Screen.ContactMoreDetails.route,
            arguments = listOf(
                navArgument("contact_id") { type = NavType.LongType },
                navArgument("is_edit") { 
                    type = NavType.BoolType
                    defaultValue = false 
                }
            )
        ) { backStackEntry ->
            val isEditMode = backStackEntry.arguments?.getBoolean("is_edit") ?: false
            val contactMoreDetailsViewModel: ContactMoreDetailsViewModel = koinViewModel()
            
            ContactMoreDetailsRoute(
                isEditMode = isEditMode,
                onBackClick = onBack,
                onSaveSuccess = {
                    if (isEditMode) {
                        navController.popBackStack()
                    } else {
                        navController.popBackStack(Screen.AddContact.route, inclusive = true)
                    }
                },
                viewModel = contactMoreDetailsViewModel
            )
        }

        composable(
            route = Screen.ContactDetails.route,
            arguments = listOf(
                navArgument("contact_id") { type = NavType.LongType },
                navArgument("contact_tag") { type = NavType.StringType }
            )
        ) {
            ContactDetailsRoute(
                onBackClick = onBack,
                onEditClick = { id ->
                    navController.navigate(Screen.ContactMoreDetails.createRoute(id, isEdit = true))
                }
            )
        }

        composable (
            route = Screen.ProfileSetup.route
        ){
            ProfileSetupRoute(
                onBackClick = onBack,
                onSaveSuccess = {
                    if (navController.previousBackStackEntry?.destination?.route == Screen.Account.route) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Screen.SocialProfileSetup.route)
                    }
                }
            )
        }

        composable(
            route = Screen.SocialProfileSetup.route
        ){
            ProfileSetupSocialRoute(
                onBackClick = onBack,
                onSaveSuccess = {
                    if (navController.previousBackStackEntry?.destination?.route == Screen.Account.route) {
                        navController.popBackStack()
                    } else {
                        viewModel.completeProfileSetup()
                        navController.navigate(Screen.Home.route){
                            popUpTo(Screen.SocialProfileSetup.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Account.route) {
            AccountRoute(
                onBackClick = onBack,
                onWorkProfileClick = { navController.navigate(Screen.ProfileSetup.route) },
                onSocialProfileClick = { navController.navigate(Screen.SocialProfileSetup.route) },
                onPrivacyPolicyClick = { /* Handle privacy policy */ }
            )
        }

        composable(Screen.Scan.route) {
            ScanScreen(
                onBackClick = onBack,
                onScanResult = { result ->
                    android.util.Log.d("DEBUG_SCAN", "Step 2: MainActivity received result, setting in ScanResultManager")
                    ScanResultManager.setResult(result)
                    
                    navController.navigate(Screen.ScannedContact.route) {
                        popUpTo(Screen.Scan.route) { inclusive = true }
                    }
                    android.util.Log.d("DEBUG_SCAN", "Step 3: Navigation triggered")
                }
            )
        }

        composable(Screen.ScannedContact.route) {
            ScannedContactRoute(
                onBackClick = onBack,
                onDoneClick = { navController.popBackStack(Screen.Home.route, false) },
                onViewContactClick = { id, tag ->
                    navController.navigate(Screen.ContactDetails.createRoute(id, tag)) {
                        popUpTo(Screen.ScannedContact.route) { inclusive = true }
                    }
                },
                onNavigateToError = {
                    navController.navigate(Screen.ScanError.route) {
                        popUpTo(Screen.ScannedContact.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ScanError.route) {
            ScanErrorScreen(
                onRetryClick = {
                    navController.navigate(Screen.Scan.route) {
                        popUpTo(Screen.ScanError.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack(Screen.Home.route, false)
                }
            )
        }

        composable(
            route = Screen.QRCodeShare.route,
            arguments = listOf(
                navArgument("profile_type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val profileType = backStackEntry.arguments?.getString("profile_type") ?: "WORK"
            QRCodeShareScreenRoute(
                profileType = profileType,
                onBackClick = onBack
            )
        }
    }
}
