package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.AppScreen
import com.example.ui.CallViewModel
import com.example.ui.components.GamingCard
import com.example.ui.theme.CallRed
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingCardBg
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingDarkBg
import com.example.ui.theme.GamingElevatedSurface
import com.example.ui.theme.GamingErrorRed
import com.example.ui.theme.GamingPrimaryPurple
import com.example.ui.theme.GamingSecondaryBg
import com.example.ui.theme.GamingTextMuted
import com.example.ui.theme.GamingTextPrimary
import com.example.ui.theme.GamingTextSecondary

@Composable
fun SettingsScreen(
    viewModel: CallViewModel,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.selectBottomTab(AppScreen.DASHBOARD)
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
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GamingTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account Group
            Text(
                text = "Account",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GamingCyan,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            GamingCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        onClick = { viewModel.selectBottomTab(AppScreen.PROFILE) }
                    )
                    HorizontalDivider(color = GamingBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
                    SettingsRowItem(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        onClick = {
                            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                            val userEmail = auth.currentUser?.email
                            if (!userEmail.isNullOrBlank()) {
                                auth.sendPasswordResetEmail(userEmail)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Password reset email sent to $userEmail!", Toast.LENGTH_LONG).show()
                                    }
                                    .addOnFailureListener { err: Exception ->
                                        Toast.makeText(context, "Error: ${err.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                Toast.makeText(context, "Guest mode active. Please sign in with Google/Email to change password.", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                    HorizontalDivider(color = GamingBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
                    SettingsRowItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        onClick = { viewModel.toggleNotificationsSheet() }
                    )
                    HorizontalDivider(color = GamingBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
                    SettingsRowItem(
                        icon = Icons.Default.Security,
                        title = "Privacy & Safety",
                        onClick = {
                            Toast.makeText(context, "Voice & signaling are end-to-end encrypted", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Group
            Text(
                text = "App",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GamingCyan,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            GamingCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.DarkMode,
                        title = "Appearance",
                        value = "Dark",
                        onClick = { }
                    )
                    HorizontalDivider(color = GamingBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
                    SettingsRowItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        value = "English",
                        onClick = { }
                    )
                    HorizontalDivider(color = GamingBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
                    SettingsRowItem(
                        icon = Icons.Default.GraphicEq,
                        title = "Audio and Video",
                        onClick = {
                            Toast.makeText(context, "Echo cancellation & noise suppression are active", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = GamingBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
                    SettingsRowItem(
                        icon = Icons.AutoMirrored.Filled.Help,
                        title = "Help & Support",
                        onClick = {
                            Toast.makeText(context, "Gaming VC Support: support@gamingvc.app", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = GamingBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
                    SettingsRowItem(
                        icon = Icons.Default.Info,
                        title = "About",
                        value = "v1.2.0",
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button matching design
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = GamingCardBg,
                    contentColor = GamingErrorRed
                ),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, GamingErrorRed.copy(alpha = 0.6f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = GamingErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Logout",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingErrorRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = GamingSecondaryBg,
            title = {
                Text("Log Out", fontWeight = FontWeight.Bold, color = GamingTextPrimary)
            },
            text = {
                Text(
                    text = "Are you sure you want to log out of Gaming VC?",
                    color = GamingTextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GamingErrorRed)
                ) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = GamingTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = GamingCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GamingTextPrimary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 13.sp,
                    color = GamingTextMuted
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GamingTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
