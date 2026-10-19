package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PresentToAll
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import com.example.data.model.FriendItem
import com.example.data.model.FriendRequest
import com.example.service.ScreenRecordService
import com.example.util.PermissionManager
import com.example.util.PermissionRationaleDialog
import com.example.util.PermissionSettingsDialog
import com.example.util.rememberRecordingPermissionState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.core.content.ContextCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.RoomParticipant
import com.example.media.CameraManager
import com.example.ui.CallViewModel
import com.example.ui.theme.ActiveGreen
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CallRed
import com.example.ui.theme.CardNavy
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.RoyalViolet
import com.example.ui.theme.SlateNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.VibrantCyan
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveCallScreen(
    viewModel: CallViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentUserId = Firebase.auth.currentUser?.uid ?: ""

    val coroutineScope = rememberCoroutineScope()
    var showLeaveConfirmDialog by remember { mutableStateOf(false) }
    var chatMessageInput by remember { mutableStateOf("") }

    // Camera manager for local feed in call
    val cameraManager = remember { CameraManager(context) }
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.release()
        }
    }

    val mediaProjectionManager = remember {
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager
    }

    val screenCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val presenterName = Firebase.auth.currentUser?.displayName
                ?: if (uiState.isGuestMode) "Guest User" else "You"
            viewModel.setScreenSharing(true, presenterName)
            Toast.makeText(context, "Broadcasting display to meeting", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Screen sharing cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    val screenRecordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val displayMetrics = context.resources.displayMetrics
            ScreenRecordService.startService(
                context = context,
                resultCode = result.resultCode,
                resultData = result.data!!,
                width = displayMetrics.widthPixels,
                height = displayMetrics.heightPixels,
                density = displayMetrics.densityDpi
            )
            Toast.makeText(context, "Screen & audio recording service started", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Screen recording permission cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    var pendingRecordingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val recordingPermissionState = rememberRecordingPermissionState {
        pendingRecordingAction?.invoke()
        pendingRecordingAction = null
    }

    BackHandler {
        showLeaveConfirmDialog = true
    }

    val isCameraActive by cameraManager.isCameraActive.collectAsState()
    val isMicMuted by cameraManager.isMicMuted.collectAsState()

    // Filter remote participants vs local
    val otherParticipants = uiState.participants.filter { it.userId != currentUserId }
    val totalInCall = uiState.participants.size.coerceAtLeast(1)

    // Format duration string
    val mins = uiState.callDurationSeconds / 60
    val secs = uiState.callDurationSeconds % 60
    val durationText = String.format("%02d:%02d", mins, secs)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavy)
    ) {
        // --- 1. Video Grid / Participant Area ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp, top = 76.dp, start = 12.dp, end = 12.dp)
        ) {
            when {
                // Scenario A: Screen Sharing Active (Local or Remote)
                uiState.isScreenSharing || uiState.screenSharePresenterName != null -> {
                    val presenter = uiState.screenSharePresenterName ?: "You"
                    val isLocal = uiState.isScreenSharing
                    ScreenShareView(
                        sharerName = if (isLocal) "You are presenting" else "$presenter is presenting",
                        isLocal = isLocal,
                        onStopSharing = {
                            viewModel.setScreenSharing(false)
                            Toast.makeText(context, "Stopped presenting screen", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Scenario B: Alone in room (Waiting for peers)
                otherParticipants.isEmpty() -> {
                    WaitingForPeersView(
                        roomId = uiState.targetRoomId,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Meeting Link", uiState.targetRoomId))
                            viewModel.notifyInviteCopied()
                            Toast.makeText(context, "Room code copied!", Toast.LENGTH_SHORT).show()
                        },
                        copied = uiState.inviteCopiedNotice,
                        previewViewFactory = { ctx ->
                            PreviewView(ctx).also { pv ->
                                previewViewRef = pv
                                cameraManager.bindCamera(lifecycleOwner, pv)
                            }
                        },
                        isCameraActive = isCameraActive
                    )
                }

                // Scenario C: 1 Remote Peer (2 total) -> Full view + Floating PIP
                otherParticipants.size == 1 -> {
                    val peer = otherParticipants[0]
                    Box(modifier = Modifier.fillMaxSize()) {
                        RemoteParticipantTile(
                            participant = peer,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Floating Local PIP in bottom-right corner
                        LocalPipTile(
                            previewViewFactory = { ctx ->
                                PreviewView(ctx).also { pv ->
                                    previewViewRef = pv
                                    cameraManager.bindCamera(lifecycleOwner, pv)
                                }
                            },
                            isCameraActive = isCameraActive,
                            isMicMuted = isMicMuted,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                        )
                    }
                }

                // Scenario D: 2+ Remote Peers (3+ total) -> Multi-participant Grid
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Local tile
                        item {
                            LocalGridTile(
                                previewViewFactory = { ctx ->
                                    PreviewView(ctx).also { pv ->
                                        previewViewRef = pv
                                        cameraManager.bindCamera(lifecycleOwner, pv)
                                    }
                                },
                                isCameraActive = isCameraActive,
                                isMicMuted = isMicMuted
                            )
                        }

                        // Remote tiles
                        items(otherParticipants) { peer ->
                            RemoteParticipantTile(
                                participant = peer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(3f / 4f)
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Top Header Overlay ---
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 16.dp, start = 12.dp, end = 12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.80f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side: Friend Requests / VC Button and Room Code
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Friend request VC button on left top side
                BadgedBox(
                    badge = {
                        val badgeCount = uiState.incomingFriendRequests.size + uiState.incomingCallInvitations.size
                        if (badgeCount > 0) {
                            Badge(containerColor = CallRed) {
                                Text(badgeCount.toString(), color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = { viewModel.toggleFriendsSheet() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(RoyalViolet)
                            .testTag("incall_friends_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Friend Request & VC Invite",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = uiState.currentRoom?.title ?: "Meeting",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = uiState.targetRoomId,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = VibrantCyan
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy code",
                            tint = VibrantCyan,
                            modifier = Modifier
                                .size(12.dp)
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Meeting Code", uiState.targetRoomId))
                                    viewModel.notifyInviteCopied()
                                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                                }
                        )
                    }
                }
            }

            // Status Badges & Top Leave Call Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (uiState.isRecording) {
                    val recMins = uiState.recordingDurationSeconds / 60
                    val recSecs = uiState.recordingDurationSeconds % 60
                    val recDurationText = String.format("%02d:%02d", recMins, recSecs)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CallRed)
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "REC $recDurationText",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (uiState.isScreenRecording) {
                    val sRecMins = uiState.screenRecordingDurationSeconds / 60
                    val sRecSecs = uiState.screenRecordingDurationSeconds % 60
                    val sRecDurationText = String.format("%02d:%02d", sRecMins, sRecSecs)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CallRed)
                            .clickable { ScreenRecordService.stopService(context) }
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SCREEN $sRecDurationText",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (uiState.isScreenSharing) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(RoyalViolet)
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PresentToAll,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Sharing",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                // Duration Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardNavy)
                        .padding(horizontal = 7.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ActiveGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = durationText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                // Participant count pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardNavy)
                        .clickable { viewModel.toggleParticipantList() }
                        .padding(horizontal = 7.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = VibrantCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = totalInCall.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Connection Quality Indicator (Signal Bars)
                NetworkQualityIndicator(
                    bars = uiState.networkQualityBars,
                    pingMs = uiState.networkPingMs
                )

                // Top Header Leave Call Button
                Button(
                    onClick = { showLeaveConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CallRed),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("top_leave_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "Leave Call",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Leave",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- 3. Bottom Control Toolbar ---
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 12.dp, end = 12.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.Black.copy(alpha = 0.85f))
                .border(1.dp, BorderNavy, RoundedCornerShape(32.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mic Toggle
            IconButton(
                onClick = {
                    cameraManager.toggleMic()
                    viewModel.toggleInCallMic()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isMicMuted) CallRed else CardNavy)
                    .testTag("incall_mic_button")
            ) {
                Icon(
                    imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = if (isMicMuted) "Unmute mic" else "Mute mic",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Camera Toggle
            IconButton(
                onClick = {
                    previewViewRef?.let { pv ->
                        cameraManager.toggleCamera(lifecycleOwner, pv)
                    }
                    viewModel.toggleInCallVideo()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (!isCameraActive) CallRed else CardNavy)
                    .testTag("incall_video_button")
            ) {
                Icon(
                    imageVector = if (isCameraActive) Icons.Default.Videocam else Icons.Default.VideocamOff,
                    contentDescription = if (isCameraActive) "Turn camera off" else "Turn camera on",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Lens Flip
            IconButton(
                onClick = {
                    previewViewRef?.let { pv ->
                        cameraManager.switchLens(lifecycleOwner, pv)
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CardNavy)
                    .testTag("incall_switch_lens_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch camera",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Screen Share Toggle via MediaProjection
            IconButton(
                onClick = {
                    if (uiState.isScreenSharing) {
                        viewModel.setScreenSharing(false)
                        Toast.makeText(context, "Stopped screen broadcast", Toast.LENGTH_SHORT).show()
                    } else {
                        mediaProjectionManager?.let { mpm ->
                            try {
                                screenCaptureLauncher.launch(mpm.createScreenCaptureIntent())
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot launch screen capture: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        } ?: run {
                            Toast.makeText(context, "Screen projection not supported on this device", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isScreenSharing) RoyalViolet else CardNavy)
                    .testTag("incall_screenshare_button")
            ) {
                Icon(
                    imageVector = if (uiState.isScreenSharing) Icons.Default.StopScreenShare else Icons.Default.PresentToAll,
                    contentDescription = if (uiState.isScreenSharing) "Stop screen sharing" else "Share screen presentation",
                    tint = if (uiState.isScreenSharing) Color.White else VibrantCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Record Call Audio & Video Toggle
            IconButton(
                onClick = {
                    if (uiState.isRecording) {
                        cameraManager.stopRecording()
                        viewModel.setRecordingActive(false)
                    } else {
                        if (!PermissionManager.hasCameraPermission(context)) {
                            Toast.makeText(context, "Camera permission needed to record", Toast.LENGTH_SHORT).show()
                            return@IconButton
                        }
                        pendingRecordingAction = {
                            cameraManager.startRecording(
                                scope = coroutineScope,
                                onDurationUpdate = { dur: Long -> viewModel.setRecordingDuration(dur) },
                                onRecordingStarted = { viewModel.setRecordingActive(true) },
                                onRecordingFinalized = { uri: Uri? -> viewModel.onRecordingSaved(uri?.toString()) },
                                onError = { err: String ->
                                    viewModel.setRecordingActive(false)
                                    Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                        recordingPermissionState.checkAndRequest()
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isRecording) CallRed else CardNavy)
                    .testTag("incall_record_button")
            ) {
                Icon(
                    imageVector = if (uiState.isRecording) Icons.Default.StopCircle else Icons.Default.FiberManualRecord,
                    contentDescription = if (uiState.isRecording) "Stop Recording" else "Record Call",
                    tint = if (uiState.isRecording) Color.White else CallRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Screen & Audio Recording Service Toggle
            IconButton(
                onClick = {
                    if (uiState.isScreenRecording) {
                        ScreenRecordService.stopService(context)
                        Toast.makeText(context, "Stopping screen recording service...", Toast.LENGTH_SHORT).show()
                    } else {
                        pendingRecordingAction = {
                            mediaProjectionManager?.let { mpm ->
                                try {
                                    screenRecordLauncher.launch(mpm.createScreenCaptureIntent())
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot launch screen recorder: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            } ?: run {
                                Toast.makeText(context, "MediaProjection not supported on this device", Toast.LENGTH_SHORT).show()
                            }
                        }
                        recordingPermissionState.checkAndRequest()
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isScreenRecording) CallRed else CardNavy)
                    .testTag("incall_screen_record_button")
            ) {
                Icon(
                    imageVector = if (uiState.isScreenRecording) Icons.Default.StopCircle else Icons.Default.ScreenShare,
                    contentDescription = if (uiState.isScreenRecording) "Stop Screen Recording" else "Record Screen and Audio",
                    tint = if (uiState.isScreenRecording) Color.White else VibrantCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            // In-Call Chat Button
            BadgedBox(
                badge = {
                    if (uiState.chatMessages.isNotEmpty()) {
                        Badge(containerColor = ElectricViolet) {
                            Text(uiState.chatMessages.size.toString(), color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            ) {
                IconButton(
                    onClick = { viewModel.toggleChatDrawer() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (uiState.isChatDrawerOpen) RoyalViolet else CardNavy)
                        .testTag("incall_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "In-call chat",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Leave / End Call Button (Crimson Red)
            Button(
                onClick = { showLeaveConfirmDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CallRed),
                shape = RoundedCornerShape(22.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier
                    .height(44.dp)
                    .testTag("incall_leave_call_button")
                    .testTag("incall_end_call_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "Leave Call",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Leave",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }

        // --- 4. In-Call Chat Sheet ---
        if (uiState.isChatDrawerOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleChatDrawer() },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = SlateNavy
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "In-Call Chat",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Message list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.chatMessages.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No messages yet. Send a message to participants!",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        } else {
                            items(uiState.chatMessages) { msg ->
                                val isMe = msg.senderId == currentUserId
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                                ) {
                                    Text(
                                        text = if (isMe) "You" else msg.senderName,
                                        fontSize = 11.sp,
                                        color = if (isMe) VibrantCyan else TextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isMe) RoyalViolet else CardNavy)
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = msg.text,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatMessageInput,
                            onValueChange = { chatMessageInput = it },
                            placeholder = { Text("Send message...", color = TextTertiary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = BorderNavy,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (chatMessageInput.isNotBlank()) {
                                    viewModel.sendChatMessage(chatMessageInput)
                                    chatMessageInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(RoyalViolet)
                                .testTag("send_chat_message_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // --- 5. Participant List Sheet ---
        if (uiState.isParticipantListOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleParticipantList() },
                containerColor = SlateNavy
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Participants (${uiState.participants.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.participants) { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CardNavy)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(RoyalViolet),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = p.displayName.take(1).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (p.userId == currentUserId) "${p.displayName} (You)" else p.displayName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextPrimary
                                            )
                                            if (p.isHost) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(VibrantCyan.copy(alpha = 0.2f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "Host",
                                                        fontSize = 10.sp,
                                                        color = VibrantCyan,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(
                                        imageVector = if (p.isAudioMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = if (p.isAudioMuted) CallRed else ActiveGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Icon(
                                        imageVector = if (p.isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = if (p.isVideoOff) CallRed else ActiveGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 6. Leave Confirmation Dialog ---
        if (showLeaveConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLeaveConfirmDialog = false },
                title = { Text("Leave Meeting?", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "Are you sure you want to disconnect from this call?",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLeaveConfirmDialog = false
                            val isHost = uiState.currentRoom?.hostId == currentUserId
                            viewModel.leaveCall(isHostEnding = isHost)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CallRed),
                        modifier = Modifier.testTag("confirm_leave_call_button")
                    ) {
                        Text("Leave Call", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLeaveConfirmDialog = false }) {
                        Text("Stay", color = TextSecondary)
                    }
                },
                containerColor = SlateNavy
            )
        }

        // --- 7. Recording Saved Notification Banner ---
        AnimatedVisibility(
            visible = uiState.recordingNotice != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateNavy),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, VibrantCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ActiveGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Call Recording Saved",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = uiState.recordingNotice ?: "Saved to Movies/VideoCall",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (uiState.lastSavedRecordingUri != null) {
                        TextButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(Uri.parse(uiState.lastSavedRecordingUri), "video/mp4")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Saved to device Movies/VideoCall", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("View", color = VibrantCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    IconButton(
                        onClick = { viewModel.dismissRecordingNotice() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // --- 8. Screen Recording Saved Notification Banner ---
        AnimatedVisibility(
            visible = uiState.screenRecordingNotice != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateNavy),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, VibrantCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ActiveGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Screen Recording Saved",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = uiState.screenRecordingNotice ?: "Saved to Movies/VideoCall",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (uiState.lastSavedScreenRecordingUri != null) {
                        TextButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(Uri.parse(uiState.lastSavedScreenRecordingUri), "video/mp4")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Saved to device Movies/VideoCall", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("View", color = VibrantCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    IconButton(
                        onClick = { viewModel.dismissScreenRecordingNotice() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // --- 9. Centralized Permission Rationale & Settings Dialogs ---
        if (recordingPermissionState.showRationaleDialog) {
            PermissionRationaleDialog(
                onConfirm = recordingPermissionState.onConfirmRationale,
                onDismiss = recordingPermissionState.onDismissRationale
            )
        }

        if (recordingPermissionState.showSettingsDialog) {
            PermissionSettingsDialog(
                onOpenSettings = recordingPermissionState.onOpenSettings,
                onDismiss = recordingPermissionState.onDismissSettings
            )
        }

        // --- 10. In-Call Friend Request & VC Invitation Notice Banner ---
        AnimatedVisibility(
            visible = uiState.friendNotice != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateNavy),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, VibrantCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = VibrantCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = uiState.friendNotice ?: "",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.dismissFriendNotice() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // --- 11. In-Call Friends & VC Invite Sheet ---
        if (uiState.isFriendsSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleFriendsSheet() },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = SlateNavy
            ) {
                InCallFriendsSheetContent(
                    uiState = uiState,
                    onSendFriendRequest = { targetUid -> viewModel.sendFriendRequest(targetUid) },
                    onAcceptRequest = { req -> viewModel.acceptFriendRequest(req) },
                    onDeclineRequest = { reqId -> viewModel.declineFriendRequest(reqId) },
                    onInviteFriend = { friend -> viewModel.inviteFriendToCurrentCall(friend) },
                    onClose = { viewModel.toggleFriendsSheet() }
                )
            }
        }
    }
}

@Composable
fun InCallFriendsSheetContent(
    uiState: com.example.ui.CallUiState,
    onSendFriendRequest: (String) -> Unit,
    onAcceptRequest: (FriendRequest) -> Unit,
    onDeclineRequest: (String) -> Unit,
    onInviteFriend: (FriendItem) -> Unit,
    onClose: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var friendUidInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Friends & VC Invitations",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Share room or invite friends directly",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Room code copy pill
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Current Room Code", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = uiState.targetRoomId,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantCyan
                    )
                }
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Meeting Code", uiState.targetRoomId))
                        Toast.makeText(context, "Room code copied!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy Code", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Send friend request input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = friendUidInput,
                onValueChange = { friendUidInput = it },
                placeholder = { Text("Enter friend UID...", fontSize = 13.sp, color = TextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VibrantCyan,
                    unfocusedBorderColor = BorderNavy,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CardNavy,
                    unfocusedContainerColor = CardNavy
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("incall_friend_uid_input")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (friendUidInput.isNotBlank()) {
                        onSendFriendRequest(friendUidInput)
                        friendUidInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                modifier = Modifier.testTag("incall_send_friend_request_button")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pending Friend Requests section
        if (uiState.incomingFriendRequests.isNotEmpty()) {
            Text(
                text = "Pending Friend Requests (${uiState.incomingFriendRequests.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = VibrantCyan
            )
            Spacer(modifier = Modifier.height(8.dp))
            uiState.incomingFriendRequests.forEach { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(req.fromUserName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("UID: ${req.fromUserId}", fontSize = 10.sp, color = TextSecondary)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { onAcceptRequest(req) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(ActiveGreen)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Accept", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { onDeclineRequest(req.id) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(CallRed)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Decline", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Friends List section
        Text(
            text = "Friends List (${uiState.friends.size})",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.friends.isEmpty()) {
            Text(
                text = "No friends added yet. Enter your friend's UID above to send a request!",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(uiState.friends) { friend ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (friend.isOnline) ActiveGreen else TextSecondary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(friend.friendName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("UID: ${friend.friendUserId}", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                            Button(
                                onClick = { onInviteFriend(friend) },
                                colors = ButtonDefaults.buttonColors(containerColor = VibrantCyan),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = MidnightNavy, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Invite to VC", color = MidnightNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WaitingForPeersView(
    roomId: String,
    onCopy: () -> Unit,
    copied: Boolean,
    previewViewFactory: (Context) -> PreviewView,
    isCameraActive: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .border(2.dp, BorderNavy, RoundedCornerShape(24.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isCameraActive) {
                AndroidView(
                    factory = previewViewFactory,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CardNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(RoyalViolet.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
            }

            // Waiting notice card overlay
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(1.dp, BorderNavy, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(RoyalViolet),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Waiting for others to join",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Share the room code with participants:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardNavy)
                        .clickable { onCopy() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = roomId,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = VibrantCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (copied) "Copied!" else "Copy",
                        fontSize = 12.sp,
                        color = VibrantCyan
                    )
                }
            }
        }
    }
}

@Composable
fun RemoteParticipantTile(
    participant: RoomParticipant,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateNavy),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, BorderNavy, RoundedCornerShape(18.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Video / Avatar placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(CardNavy, MidnightNavy)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(ElectricViolet, VibrantCyan))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = participant.displayName.take(1).uppercase(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = participant.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }

            // Name & Status Pill
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = participant.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                if (participant.isAudioMuted) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.MicOff,
                        contentDescription = "Muted",
                        tint = CallRed,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LocalPipTile(
    previewViewFactory: (Context) -> PreviewView,
    isCameraActive: Boolean,
    isMicMuted: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .width(105.dp)
            .height(145.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, ElectricViolet, RoundedCornerShape(16.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isCameraActive) {
                AndroidView(
                    factory = previewViewFactory,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CardNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Local label
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "You",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                if (isMicMuted) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.MicOff,
                        contentDescription = "Muted",
                        tint = CallRed,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LocalGridTile(
    previewViewFactory: (Context) -> PreviewView,
    isCameraActive: Boolean,
    isMicMuted: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, ElectricViolet, RoundedCornerShape(18.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isCameraActive) {
                AndroidView(
                    factory = previewViewFactory,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CardNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Local label pill
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "You",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (isMicMuted) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.MicOff,
                        contentDescription = "Muted",
                        tint = CallRed,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenShareView(
    sharerName: String,
    isLocal: Boolean,
    onStopSharing: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateNavy),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, if (isLocal) ElectricViolet else VibrantCyan, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(if (isLocal) RoyalViolet.copy(alpha = 0.35f) else VibrantCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PresentToAll,
                    contentDescription = null,
                    tint = if (isLocal) ElectricViolet else VibrantCyan,
                    modifier = Modifier.size(42.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = sharerName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isLocal)
                    "You are broadcasting your device display live to all attendees."
                else
                    "Live screen presentation broadcast is currently active.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (isLocal) {
                Button(
                    onClick = onStopSharing,
                    colors = ButtonDefaults.buttonColors(containerColor = CallRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.StopScreenShare,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Stop Presenting", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardNavy,
                    border = BorderStroke(1.dp, BorderNavy)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ActiveGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Receiving presentation stream",
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkQualityIndicator(
    bars: Int = 4,
    pingMs: Int = 24,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardNavy)
            .padding(horizontal = 7.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Subtle signal bars
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(1.5.dp),
            modifier = Modifier.height(11.dp)
        ) {
            val barHeights = listOf(4.dp, 6.dp, 8.dp, 11.dp)
            barHeights.forEachIndexed { index, h ->
                val isActive = (index + 1) <= bars.coerceIn(1, 4)
                val barColor = when {
                    !isActive -> BorderNavy
                    bars >= 3 -> ActiveGreen
                    bars == 2 -> Color(0xFFFFB300)
                    else -> CallRed
                }
                Box(
                    modifier = Modifier
                        .width(2.5.dp)
                        .height(h)
                        .clip(RoundedCornerShape(1.dp))
                        .background(barColor)
                )
            }
        }
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "${pingMs}ms",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (bars >= 3) ActiveGreen else TextSecondary
        )
    }
}
