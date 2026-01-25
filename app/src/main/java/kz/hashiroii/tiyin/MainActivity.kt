package kz.hashiroii.tiyin

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kz.hashiroii.data.network.NetworkMonitor
import kz.hashiroii.designsystem.navigation.TiyinBottomBar
import kz.hashiroii.designsystem.navigation.TiyinTopAppBar
import kz.hashiroii.designsystem.theme.TiyinTheme
import kz.hashiroii.domain.repository.AuthRepository
import kz.hashiroii.domain.usecase.preferences.GetPreferencesUseCase
import kz.hashiroii.navigation.Analytics
import kz.hashiroii.navigation.Groups
import kz.hashiroii.navigation.Home
import kz.hashiroii.navigation.Profile
import kz.hashiroii.navigation.Settings
import kz.hashiroii.tiyin.auth.AuthIntent
import kz.hashiroii.tiyin.auth.AuthScreen
import kz.hashiroii.tiyin.auth.AuthViewModel
import kz.hashiroii.tiyin.auth.GoogleSignInHelper
import kz.hashiroii.tiyin.navigation.TiyinNavHost
import kz.hashiroii.tiyin.ui.rememberTiyinAppState
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var getPreferencesUseCase: GetPreferencesUseCase
    
    @Inject
    lateinit var authRepository: AuthRepository
    
    @Inject
    lateinit var googleSignInHelper: GoogleSignInHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiyinAppWithTheme(
                getPreferencesUseCase = getPreferencesUseCase,
                authRepository = authRepository,
                googleSignInHelper = googleSignInHelper
            )
        }
    }
}

@Composable
private fun TiyinAppWithTheme(
    getPreferencesUseCase: GetPreferencesUseCase,
    authRepository: AuthRepository,
    googleSignInHelper: GoogleSignInHelper
) {
    val preferencesState by getPreferencesUseCase().collectAsStateWithLifecycle(
        initialValue = null
    )
    
    val isAuthenticated by authRepository.isAuthenticated.collectAsStateWithLifecycle(initialValue = false)
    var showAuthScreen by remember { mutableStateOf(false) }
    var hasSkippedAuth by remember { mutableStateOf(false) }

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated && !hasSkippedAuth) {
            showAuthScreen = true
        } else {
            showAuthScreen = false
        }
    }

    preferencesState?.let { prefs ->
        TiyinTheme(themePreference = prefs.theme) {
            if (showAuthScreen && !isAuthenticated) {
                AuthScreenWrapper(
                    authRepository = authRepository,
                    googleSignInHelper = googleSignInHelper,
                    onAuthSuccess = { showAuthScreen = false },
                    onSkip = {
                        hasSkippedAuth = true
                        showAuthScreen = false
                    }
                )
            } else {
                TiyinApp(
                    googleSignInHelper = googleSignInHelper,
                    authRepository = authRepository
                )
            }
        }
    }
}

@Composable
private fun AuthScreenWrapper(
    authRepository: AuthRepository,
    googleSignInHelper: GoogleSignInHelper,
    onAuthSuccess: () -> Unit,
    onSkip: () -> Unit
) {
    val authViewModel = androidx.hilt.navigation.compose.hiltViewModel<AuthViewModel>()
    val signInIntent = googleSignInHelper.getSignInIntent()
    
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            val idTokenResult = googleSignInHelper.handleSignInResult(result.data)
            idTokenResult.onSuccess { idToken ->
                authViewModel.onIntent(AuthIntent.SignInWithGoogle(idToken))
            }.onFailure {
                authViewModel.onIntent(AuthIntent.SignInWithGoogle(""))
            }
        }
    }
    
    AuthScreen(
        onAuthSuccess = onAuthSuccess,
        onSkip = onSkip,
        onSignInClick = {
            signInLauncher.launch(signInIntent)
        }
    )
}

@Composable
fun TiyinApp(
    googleSignInHelper: GoogleSignInHelper,
    authRepository: AuthRepository
) {
    val navController = rememberNavController()
    val appState = rememberTiyinAppState(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    val currentUser by authRepository.currentUser.collectAsStateWithLifecycle(initialValue = null)

    val currentDestination = navBackStackEntry?.destination
    val currentTopLevelDestination = appState.currentTopLevelDestination
    val shouldShowBottomBar = appState.shouldShowBottomBar
    val shouldShowBackButton = appState.shouldShowBackButton

    val topBarTitle = when (currentTopLevelDestination) {
        is Home -> stringResource(id = R.string.nav_home)
        is Analytics -> stringResource(id = R.string.nav_analytics)
        is Groups -> stringResource(id = R.string.nav_groups)
        else -> when {
            currentDestination?.route?.contains("Profile") == true ->
                stringResource(id = R.string.nav_profile)
            currentDestination?.route?.contains("Settings") == true ->
                stringResource(id = R.string.nav_settings)
            else -> stringResource(id = R.string.app_name)
        }
    }
    
    val authViewModel = androidx.hilt.navigation.compose.hiltViewModel<AuthViewModel>()
    val signInIntent = googleSignInHelper.getSignInIntent()
    
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            val idTokenResult = googleSignInHelper.handleSignInResult(result.data)
            idTokenResult.onSuccess { idToken ->
                authViewModel.onIntent(AuthIntent.SignInWithGoogle(idToken))
            }.onFailure {
                authViewModel.onIntent(AuthIntent.SignInWithGoogle(""))
            }
        }
    }
    
    val onSignInClick = {
        signInLauncher.launch(signInIntent)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TiyinTopAppBar(
                title = topBarTitle,
                showBackButton = shouldShowBackButton,
                showProfileButton = appState.shouldShowSettingsIcons && !shouldShowBackButton,
                showSettingsButton = appState.shouldShowSettingsIcons && !shouldShowBackButton,
                backContentDescription = stringResource(id = R.string.nav_back),
                profileContentDescription = stringResource(id = R.string.nav_profile),
                settingsContentDescription = stringResource(id = R.string.nav_settings),
                onBackClick = { navController.navigateUp() },
                onProfileClick = { navController.navigate(Profile) },
                onSettingsClick = { navController.navigate(Settings) },
                userPhotoUrl = currentUser?.photoUrl
            )
        },
        bottomBar = {
            if (shouldShowBottomBar) {
                TiyinBottomBar(
                    currentDestination = currentTopLevelDestination,
                    homeLabel = stringResource(id = R.string.nav_home),
                    analyticsLabel = stringResource(id = R.string.nav_analytics),
                    groupsLabel = stringResource(id = R.string.nav_groups),
                    onNavigate = { destination ->
                        navController.navigate(destination) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        TiyinNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onSignInClick = onSignInClick
        )
    }
}