package com.example.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CallRed
import com.example.ui.theme.CardNavy
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.RoyalViolet
import com.example.ui.theme.SlateNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantCyan

object PermissionManager {

    fun hasAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun hasRecordingPermissions(context: Context): Boolean {
        return hasAudioPermission(context) && hasNotificationPermission(context)
    }

    fun getRecordingPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun findActivity(context: Context): Activity? {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }

    fun shouldShowRationale(activity: Activity?, permission: String): Boolean {
        return if (activity != null) {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        } else {
            false
        }
    }
}

class RecordingPermissionState(
    val checkAndRequest: () -> Unit,
    val showRationaleDialog: Boolean,
    val showSettingsDialog: Boolean,
    val onDismissRationale: () -> Unit,
    val onConfirmRationale: () -> Unit,
    val onDismissSettings: () -> Unit,
    val onOpenSettings: () -> Unit
)

@Composable
fun rememberRecordingPermissionState(
    onPermissionsGranted: () -> Unit
): RecordingPermissionState {
    val context = LocalContext.current
    val activity = remember(context) { PermissionManager.findActivity(context) }

    var showRationale by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var requestedPermissionRef by remember { mutableStateOf<Array<String>>(emptyArray()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        if (allGranted) {
            onPermissionsGranted()
        } else {
            // Check if any denied permission was permanently denied
            var isPermanentlyDenied = false
            for ((perm, granted) in results) {
                if (!granted) {
                    val rationaleNeeded = PermissionManager.shouldShowRationale(activity, perm)
                    if (!rationaleNeeded) {
                        isPermanentlyDenied = true
                        break
                    }
                }
            }
            if (isPermanentlyDenied) {
                showSettings = true
            } else {
                Toast.makeText(
                    context,
                    "Recording audio and notification permissions are required to record calls",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val requestPermissions = {
        if (PermissionManager.hasRecordingPermissions(context)) {
            onPermissionsGranted()
        } else {
            requestedPermissionRef = PermissionManager.getRecordingPermissions()
            showRationale = true
        }
    }

    return RecordingPermissionState(
        checkAndRequest = requestPermissions,
        showRationaleDialog = showRationale,
        showSettingsDialog = showSettings,
        onDismissRationale = { showRationale = false },
        onConfirmRationale = {
            showRationale = false
            permissionLauncher.launch(requestedPermissionRef)
        },
        onDismissSettings = { showSettings = false },
        onOpenSettings = {
            showSettings = false
            PermissionManager.openAppSettings(context)
        }
    )
}

@Composable
fun PermissionRationaleDialog(
    title: String = "Permissions Required for Recording",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RoyalViolet.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = VibrantCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "To record your video call display and audio, VideoCall requires the following access:",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                PermissionRationaleItem(
                    icon = Icons.Default.Mic,
                    title = "Microphone Audio",
                    description = "Records meeting speech and participants with AAC compression."
                )

                Spacer(modifier = Modifier.height(10.dp))

                PermissionRationaleItem(
                    icon = Icons.Default.Notifications,
                    title = "Foreground Alerts",
                    description = "Provides persistent recording status and a quick 1-tap stop button in the status bar."
                )

                Spacer(modifier = Modifier.height(10.dp))

                PermissionRationaleItem(
                    icon = Icons.Default.Videocam,
                    title = "MediaStore Scoped Storage",
                    description = "Saves .mp4 files directly to Movies/VideoCall without broad file system access."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_permission_rationale_button")
            ) {
                Text("Continue", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now", color = TextSecondary)
            }
        },
        containerColor = SlateNavy
    )
}

@Composable
fun PermissionRationaleItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(RoyalViolet),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun PermissionSettingsDialog(
    title: String = "Permissions Needed in Settings",
    description: String = "Microphone and notification access are disabled. Please enable them in App Settings to allow VideoCall to record meetings.",
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = description,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onOpenSettings,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("open_app_settings_button")
            ) {
                Text("Open Settings", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = SlateNavy
    )
}
