package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.CallViewModel
import com.example.ui.components.GamerAvatar
import com.example.ui.components.GamingBottomBar
import com.example.ui.components.GamingCard
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingButtonGradient
import com.example.ui.theme.GamingCardBg
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingDarkBg
import com.example.ui.theme.GamingDeepPurple
import com.example.ui.theme.GamingElevatedSurface
import com.example.ui.theme.GamingHeroGradient
import com.example.ui.theme.GamingNeonBlue
import com.example.ui.theme.GamingPrimaryPurple
import com.example.ui.theme.GamingSecondaryBg
import com.example.ui.theme.GamingSuccessGreen
import com.example.ui.theme.GamingTextMuted
import com.example.ui.theme.GamingTextPrimary
import com.example.ui.theme.GamingTextSecondary

@Composable
fun ProfileScreen(
    viewModel: CallViewModel,
    onSignOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(uiState.myDisplayName.ifBlank { "Raj Kumari" }) }
    var editBio by remember { mutableStateOf(uiState.userBio) }
    var editHandle by remember { mutableStateOf(uiState.userHandle) }

    BackHandler {
        viewModel.selectBottomTab(AppScreen.DASHBOARD)
    }

    Scaffold(
        bottomBar = {
            GamingBottomBar(
                currentTab = AppScreen.PROFILE,
                onTabSelected = { tab -> viewModel.selectBottomTab(tab) },
                unreadFriendsCount = uiState.incomingFriendRequests.size
            )
        },
        containerColor = GamingDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GamingDarkBg)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
            }

            // Cyberpunk Profile Hero Banner & Avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Banner background
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(GamingHeroGradient)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            drawCircle(
                                color = GamingCyan.copy(alpha = 0.2f),
                                radius = w * 0.35f,
                                center = Offset(w * 0.8f, h * 0.4f)
                            )
                        }
                    }
                }

                // Avatar and Gamer Info (Overlapping Card)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 70.dp, start = 14.dp, end = 14.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        GamerAvatar(
                            name = uiState.myDisplayName.ifBlank { "Raj Kumari" },
                            size = 80.dp,
                            isOnline = true
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.padding(bottom = 6.dp)) {
                            Text(
                                text = uiState.myDisplayName.ifBlank { "Raj Kumari" },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingTextPrimary
                            )
                            Text(
                                text = uiState.userHandle.ifBlank { "@rajkumari" },
                                fontSize = 13.sp,
                                color = GamingTextSecondary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(GamingSuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Online",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GamingSuccessGreen
                                )
                                if (uiState.myUserId.isNotBlank()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "• ${uiState.myUserId}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = GamingCyan
                                    )
                                }
                            }
                        }
                    }

                    // Edit Profile Button
                    OutlinedButton(
                        onClick = {
                            editName = uiState.myDisplayName.ifBlank { "Raj Kumari" }
                            editBio = uiState.userBio
                            editHandle = uiState.userHandle
                            showEditProfileDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = GamingElevatedSurface.copy(alpha = 0.8f),
                            contentColor = GamingTextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder),
                        modifier = Modifier
                            .height(38.dp)
                            .padding(bottom = 6.dp)
                            .testTag("edit_profile_button")
                    ) {
                        Text("Edit Profile", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Stats Cards Row: Rooms Joined (12) | Friends (8) | Games Played (3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(
                    number = uiState.roomsJoinedCount.toString(),
                    label = "Rooms Joined",
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    number = if (uiState.friends.isNotEmpty()) uiState.friends.size.toString() else uiState.friendsTotalCount.toString(),
                    label = "Friends",
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    number = uiState.gamesPlayedCount.toString(),
                    label = "Games Played",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // My Games Section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "My Games",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = GamingTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GamerGameBadgeCard(
                        game = "BGMI",
                        gradient = Brush.linearGradient(listOf(Color(0xFF7C2D12), Color(0xFFC2410C)))
                    )
                    GamerGameBadgeCard(
                        game = "Valorant",
                        gradient = Brush.linearGradient(listOf(Color(0xFF881337), Color(0xFFE11D48)))
                    )
                    GamerGameBadgeCard(
                        game = "Free Fire",
                        gradient = Brush.linearGradient(listOf(Color(0xFF78350F), Color(0xFFD97706)))
                    )
                    GamerGameBadgeCard(
                        game = "Minecraft",
                        gradient = Brush.linearGradient(listOf(Color(0xFF064E3B), Color(0xFF059669)))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation List Items matching Mockup:
            // My Rooms, My Friends, My Achievements, Settings
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.MeetingRoom,
                    title = "My Rooms",
                    onClick = { viewModel.selectBottomTab(AppScreen.ROOM_DISCOVERY) }
                )

                ProfileMenuItem(
                    icon = Icons.Default.People,
                    title = "My Friends",
                    badge = if (uiState.incomingFriendRequests.isNotEmpty()) "${uiState.incomingFriendRequests.size} new" else null,
                    onClick = { viewModel.selectBottomTab(AppScreen.FRIENDS) }
                )

                ProfileMenuItem(
                    icon = Icons.Default.EmojiEvents,
                    title = "My Achievements",
                    onClick = { /* View achievements */ }
                )

                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Settings",
                    onClick = { viewModel.navigateTo(AppScreen.SETTINGS) }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = GamingSecondaryBg,
            title = {
                Text("Edit Profile", fontWeight = FontWeight.Bold, color = GamingTextPrimary)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name", color = GamingTextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingCyan,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = GamingTextPrimary,
                            unfocusedTextColor = GamingTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editHandle,
                        onValueChange = { editHandle = it },
                        label = { Text("Username / Handle", color = GamingTextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingCyan,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = GamingTextPrimary,
                            unfocusedTextColor = GamingTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio", color = GamingTextSecondary) },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingCyan,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = GamingTextPrimary,
                            unfocusedTextColor = GamingTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(editName, editBio, editHandle)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GamingPrimaryPurple)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = GamingTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ProfileStatCard(
    number: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GamingCardBg)
            .border(1.dp, GamingBorder, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = number,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = GamingTextPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = GamingTextSecondary
            )
        }
    }
}

@Composable
private fun GamerGameBadgeCard(
    game: String,
    gradient: Brush
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(68.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(gradient)
                .border(1.5.dp, GamingBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = game,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = game,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GamingTextSecondary,
            maxLines = 1
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    GamingCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                    fontWeight = FontWeight.SemiBold,
                    color = GamingTextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GamingPrimaryPurple)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
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
}
