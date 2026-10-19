package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.media.CameraManager
import com.example.ui.AppScreen
import com.example.ui.CallViewModel
import com.example.ui.components.GamerAvatar
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.theme.ActiveGreen
import com.example.ui.theme.CallRed
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingButtonGradient
import com.example.ui.theme.GamingCardBg
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingDarkBg
import com.example.ui.theme.GamingDeepPurple
import com.example.ui.theme.GamingElevatedSurface
import com.example.ui.theme.GamingNeonBlue
import com.example.ui.theme.GamingPrimaryPurple
import com.example.ui.theme.GamingSecondaryBg
import com.example.ui.theme.GamingSuccessGreen
import com.example.ui.theme.GamingTextMuted
import com.example.ui.theme.GamingTextPrimary
import com.example.ui.theme.GamingTextSecondary

data class LobbyMember(
    val name: String,
    val role: String,
    val isLeader: Boolean = false
)

val defaultLobbyMembers = listOf(
    LobbyMember("Hou", "Leader", isLeader = true),
    LobbyMember("Rohan", "Member"),
    LobbyMember("Priya", "Member"),
    LobbyMember("Aman", "Member")
)

@Composable
fun RoomLobby(
    viewModel: CallViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraManager = remember { CameraManager(context) }
    var activePreviewView by remember { mutableStateOf<PreviewView?>(null) }

    val roomTitle = uiState.currentRoom?.title ?: if (uiState.targetRoomId.isNotBlank()) uiState.targetRoomId else "BGMI Squad"
    val roomCategory = uiState.currentRoom?.category ?: "BGMI"

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasCameraPermission = perms[Manifest.permission.CAMERA] ?: hasCameraPermission
        hasAudioPermission = perms[Manifest.permission.RECORD_AUDIO] ?: hasAudioPermission
    }

    LaunchedEffect(Unit) {
        val needed = mutableListOf<String>()
        if (!hasCameraPermission) needed.add(Manifest.permission.CAMERA)
        if (!hasAudioPermission) needed.add(Manifest.permission.RECORD_AUDIO)
        if (needed.isNotEmpty()) {
            permissionLauncher.launch(needed.toTypedArray())
        }
    }

    BackHandler {
        viewModel.selectBottomTab(AppScreen.DASHBOARD)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar: Back + Room Title & Online + Call/More Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.selectBottomTab(AppScreen.DASHBOARD) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GamingTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = roomTitle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(GamingSuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "12 online",
                                fontSize = 11.sp,
                                color = GamingSuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.joinCallFromLobby() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GamingPrimaryPurple)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { /* More options */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = GamingTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Large Gaming Cover Image & Card with badges
            GamingCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2A154D),
                                    Color(0xFF131E3D),
                                    GamingCardBg
                                )
                            )
                        )
                ) {
                    // Cybernetic gaming background lines
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        drawCircle(
                            color = GamingCyan.copy(alpha = 0.15f),
                            radius = w * 0.4f,
                            center = Offset(w * 0.85f, h * 0.3f)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Game badge tag in center/bottom
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GamingSecondaryBg.copy(alpha = 0.85f))
                                .border(1.dp, GamingBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$roomCategory • Voice Chat • Squad",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = roomTitle,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GamingTextPrimary
                        )

                        Text(
                            text = uiState.currentRoom?.description ?: "Conqueror rank push • Mic on only",
                            fontSize = 12.sp,
                            color = GamingTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Camera / Avatar Preview Card with Mic & Cam Controls
            GamingCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(GamingSecondaryBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (!uiState.isVideoOff && hasCameraPermission) {
                        AndroidView(
                            factory = { ctx ->
                                PreviewView(ctx).also { previewView ->
                                    activePreviewView = previewView
                                    cameraManager.bindCamera(
                                        lifecycleOwner = lifecycleOwner,
                                        previewView = previewView
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            GamerAvatar(
                                name = uiState.myDisplayName.ifBlank { "You" },
                                size = 64.dp,
                                isOnline = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (uiState.isVideoOff) "Camera Off" else "Preview Ready",
                                fontSize = 12.sp,
                                color = GamingTextSecondary
                            )
                        }
                    }

                    // Floating preview control pills (Mic, Cam, Flip)
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleLobbyMic() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (uiState.isMicMuted) CallRed else GamingCardBg)
                                .testTag("lobby_mic_toggle")
                        ) {
                            Icon(
                                imageVector = if (uiState.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Toggle Mic",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleLobbyVideo() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (uiState.isVideoOff) CallRed else GamingCardBg)
                                .testTag("lobby_camera_toggle")
                        ) {
                            Icon(
                                imageVector = if (uiState.isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                contentDescription = "Toggle Video",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (!uiState.isVideoOff && hasCameraPermission) {
                            IconButton(
                                onClick = {
                                    activePreviewView?.let { preview ->
                                        cameraManager.switchLens(lifecycleOwner, preview)
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GamingCardBg)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cameraswitch,
                                    contentDescription = "Switch Camera",
                                    tint = GamingCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Room Members Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Room Members (12)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GamingTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Members Avatars Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                defaultLobbyMembers.forEach { member ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(60.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            GamerAvatar(
                                name = member.name,
                                size = 48.dp,
                                isOnline = true
                            )
                            if (member.isLeader) {
                                // Crown badge on leader avatar
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFB800)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👑", fontSize = 9.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = member.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingTextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = member.role,
                            fontSize = 10.sp,
                            color = if (member.isLeader) GamingCyan else GamingTextMuted
                        )
                    }
                }

                // "+7" extra members circle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(60.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(GamingElevatedSurface)
                            .border(1.dp, GamingBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+7",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Squad",
                        fontSize = 11.sp,
                        color = GamingTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Join Call Gradient Button
            GamingButton(
                text = "Join Call",
                icon = Icons.Default.Call,
                onClick = { viewModel.joinCallFromLobby() },
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                testTag = "join_call_button"
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Bottom Actions: Invite, Share, Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                LobbyBottomAction(
                    icon = Icons.Default.PersonAdd,
                    label = "Invite",
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Gaming Room Code", uiState.targetRoomId))
                        Toast.makeText(context, "Room code copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                )

                LobbyBottomAction(
                    icon = Icons.Default.Share,
                    label = "Share",
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Join my gaming room in Gaming VC! Room code: ${uiState.targetRoomId}")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Gaming Room")
                        context.startActivity(shareIntent)
                    }
                )

                LobbyBottomAction(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    onClick = { viewModel.navigateTo(AppScreen.SETTINGS) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LobbyScreen(
    viewModel: CallViewModel
) {
    RoomLobby(viewModel = viewModel)
}

@Composable
private fun LobbyBottomAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(GamingElevatedSurface)
                .border(1.dp, GamingBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = GamingCyan,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = GamingTextSecondary
        )
    }
}
