package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.CallViewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CreateJoinRoomScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.LiveCallScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.MeetingEndedScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RoomDiscoveryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.GamingDarkBg
import com.example.ui.theme.VideoCallTheme
import com.example.util.AppNotificationManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private val activityViewModel: CallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppNotificationManager.initNotificationChannels(this)
        handleIntent(intent)

        setContent {
            VideoCallTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = GamingDarkBg
                ) {
                    AppContent(callViewModel = activityViewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        when (intent.action) {
            AppNotificationManager.ACTION_JOIN_ROOM -> {
                val roomId = intent.getStringExtra(AppNotificationManager.EXTRA_ROOM_ID)
                val inviteId = intent.getStringExtra(AppNotificationManager.EXTRA_INVITE_ID)
                if (!roomId.isNullOrBlank()) {
                    if (!inviteId.isNullOrBlank()) {
                        activityViewModel.dismissCallInvitation(inviteId)
                    }
                    activityViewModel.prepareJoinRoom(roomId)
                }
            }
            AppNotificationManager.ACTION_DISMISS_INVITE -> {
                val inviteId = intent.getStringExtra(AppNotificationManager.EXTRA_INVITE_ID)
                if (!inviteId.isNullOrBlank()) {
                    activityViewModel.dismissCallInvitation(inviteId)
                }
            }
            AppNotificationManager.ACTION_ACCEPT_FRIEND -> {
                val requestId = intent.getStringExtra(AppNotificationManager.EXTRA_REQUEST_ID)
                val fromUserId = intent.getStringExtra(AppNotificationManager.EXTRA_FROM_USER_ID) ?: ""
                val fromUserName = intent.getStringExtra(AppNotificationManager.EXTRA_FROM_USER_NAME) ?: "Friend"
                if (!requestId.isNullOrBlank()) {
                    val req = com.example.data.model.FriendRequest(
                        id = requestId,
                        fromUserId = fromUserId,
                        fromUserName = fromUserName
                    )
                    activityViewModel.acceptFriendRequest(req)
                }
            }
            AppNotificationManager.ACTION_DECLINE_FRIEND -> {
                val requestId = intent.getStringExtra(AppNotificationManager.EXTRA_REQUEST_ID)
                if (!requestId.isNullOrBlank()) {
                    activityViewModel.declineFriendRequest(requestId)
                }
            }
        }
    }
}

@Composable
fun AppContent(
    callViewModel: CallViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { com.example.data.SessionManager(context) }
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    val uiState by callViewModel.uiState.collectAsState()

    // Request notification permissions on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* permission handled */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            if (auth.currentUser != null) {
                sessionManager.saveGoogleUser(
                    auth.currentUser!!.uid,
                    auth.currentUser!!.displayName ?: "User",
                    auth.currentUser!!.email ?: ""
                )
                callViewModel.onAuthCompleted(isGuest = false)
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    val isUserLoggedIn = currentUser != null || sessionManager.isLoggedIn || uiState.isGuestMode

    // If on SPLASH screen, show SplashScreen first
    if (uiState.currentScreen == AppScreen.SPLASH) {
        SplashScreen(
            onTimeout = {
                if (isUserLoggedIn) {
                    callViewModel.navigateTo(AppScreen.DASHBOARD)
                } else {
                    callViewModel.navigateTo(AppScreen.AUTH)
                }
            }
        )
    } else if (!isUserLoggedIn) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
                callViewModel.onAuthCompleted(isGuest = false)
                callViewModel.navigateTo(AppScreen.DASHBOARD)
            },
            onContinueAsGuest = { guestUid, guestName ->
                sessionManager.saveGuestUser(name = guestName ?: "", customUid = guestUid)
                callViewModel.onAuthCompleted(isGuest = true, guestUid = guestUid, guestName = guestName)
                callViewModel.navigateTo(AppScreen.DASHBOARD)
            }
        )
    } else {
        MainAppNavigation(
            callViewModel = callViewModel,
            onSignOut = {
                currentUser = null
                sessionManager.clearSession()
                callViewModel.onSignOutSession()
                callViewModel.navigateTo(AppScreen.AUTH)
            }
        )
    }
}

@Composable
fun MainAppNavigation(
    onSignOut: () -> Unit,
    callViewModel: CallViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { com.example.data.SessionManager(context) }
    val uiState by callViewModel.uiState.collectAsState()

    AnimatedContent(
        targetState = uiState.currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            AppScreen.SPLASH -> {
                SplashScreen(
                    onTimeout = { callViewModel.navigateTo(AppScreen.DASHBOARD) }
                )
            }
            AppScreen.AUTH,
            AppScreen.WELCOME -> {
                AuthScreen(
                    onAuthSuccess = { callViewModel.navigateTo(AppScreen.DASHBOARD) },
                    onContinueAsGuest = { guestUid, guestName ->
                        sessionManager.saveGuestUser(name = guestName ?: "", customUid = guestUid)
                        callViewModel.onAuthCompleted(isGuest = true, guestUid = guestUid, guestName = guestName)
                        callViewModel.navigateTo(AppScreen.DASHBOARD)
                    }
                )
            }
            AppScreen.DASHBOARD -> {
                DashboardScreen(viewModel = callViewModel)
            }
            AppScreen.ROOM_DISCOVERY -> {
                RoomDiscoveryScreen(viewModel = callViewModel)
            }
            AppScreen.CREATE_JOIN_ROOM,
            AppScreen.CREATE_ROOM,
            AppScreen.JOIN_ROOM -> {
                CreateJoinRoomScreen(viewModel = callViewModel)
            }
            AppScreen.LOBBY -> {
                LobbyScreen(viewModel = callViewModel)
            }
            AppScreen.LIVE_CALL -> {
                LiveCallScreen(viewModel = callViewModel)
            }
            AppScreen.FRIENDS -> {
                FriendsScreen(viewModel = callViewModel)
            }
            AppScreen.PROFILE -> {
                ProfileScreen(
                    viewModel = callViewModel,
                    onSignOut = onSignOut
                )
            }
            AppScreen.SETTINGS -> {
                SettingsScreen(
                    viewModel = callViewModel,
                    onSignOut = onSignOut
                )
            }
            AppScreen.MEETING_ENDED -> {
                MeetingEndedScreen(viewModel = callViewModel)
            }
        }
    }
}
