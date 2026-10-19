package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.CallViewModel
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingCardBg
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingDarkBg
import com.example.ui.theme.GamingDeepPurple
import com.example.ui.theme.GamingElevatedSurface
import com.example.ui.theme.GamingErrorRed
import com.example.ui.theme.GamingNeonBlue
import com.example.ui.theme.GamingPrimaryPurple
import com.example.ui.theme.GamingSecondaryBg
import com.example.ui.theme.GamingTextMuted
import com.example.ui.theme.GamingTextPrimary
import com.example.ui.theme.GamingTextSecondary

@Composable
fun CreateJoinRoomScreen(
    viewModel: CallViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var isCreateCardExpanded by remember { mutableStateOf(false) }
    var joinRoomCodeInput by remember { mutableStateOf("") }
    var joinCodeError by remember { mutableStateOf<String?>(null) }

    val categories = listOf("BGMI", "Valorant", "Free Fire", "Minecraft", "General")
    val participantLimits = listOf(4, 8, 12, 16)

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
            // Header: Back button + Title
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
                    text = "Create / Join Room",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GamingTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card 1: Create a Room
            GamingCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Glowing Room Icon
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(GamingPrimaryPurple, GamingNeonBlue)
                                )
                            )
                            .border(1.5.dp, GamingCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Create a Room",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Start your own room and invite friends or the community",
                        fontSize = 13.sp,
                        color = GamingTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Expandable Create Room Form
                    AnimatedVisibility(visible = isCreateCardExpanded) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Room Name Field
                            OutlinedTextField(
                                value = uiState.createRoomName,
                                onValueChange = { viewModel.setCreateRoomName(it) },
                                label = { Text("Room Name", color = GamingTextSecondary) },
                                placeholder = { Text("e.g. BGMI Conqueror Squad", color = GamingTextMuted) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = GamingSecondaryBg,
                                    unfocusedContainerColor = GamingSecondaryBg,
                                    focusedBorderColor = GamingCyan,
                                    unfocusedBorderColor = GamingBorder,
                                    focusedTextColor = GamingTextPrimary,
                                    unfocusedTextColor = GamingTextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Game / Category Selector
                            Text(
                                text = "Game Category",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GamingTextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.forEach { cat ->
                                    val isSelected = uiState.createRoomCategory == cat
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) GamingPrimaryPurple else GamingElevatedSurface)
                                            .border(1.dp, if (isSelected) GamingCyan else GamingBorder, RoundedCornerShape(10.dp))
                                            .clickable { viewModel.setCreateRoomCategory(cat) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else GamingTextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Max Participants Selector
                            Text(
                                text = "Max Participants",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GamingTextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                participantLimits.forEach { limit ->
                                    val isSelected = uiState.createRoomMaxParticipants == limit
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) GamingNeonBlue else GamingElevatedSurface)
                                            .border(1.dp, if (isSelected) GamingCyan else GamingBorder, RoundedCornerShape(10.dp))
                                            .clickable { viewModel.setCreateRoomMaxParticipants(limit) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$limit",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else GamingTextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Visibility Mode: Public or Private
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (!uiState.createRoomIsPrivate) GamingPrimaryPurple else GamingElevatedSurface)
                                        .border(1.dp, if (!uiState.createRoomIsPrivate) GamingCyan else GamingBorder, RoundedCornerShape(10.dp))
                                        .clickable { viewModel.setCreateRoomIsPrivate(false) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Public, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Public Room", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (uiState.createRoomIsPrivate) GamingDeepPurple else GamingElevatedSurface)
                                        .border(1.dp, if (uiState.createRoomIsPrivate) GamingCyan else GamingBorder, RoundedCornerShape(10.dp))
                                        .clickable { viewModel.setCreateRoomIsPrivate(true) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Private Room", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Button Action
                    if (!isCreateCardExpanded) {
                        GamingButton(
                            text = "Create Room",
                            onClick = { isCreateCardExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "expand_create_room_button"
                        )
                    } else {
                        GamingButton(
                            text = "Launch Room",
                            onClick = { viewModel.createGamingRoomAndJoin() },
                            isLoading = uiState.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "launch_created_room_button"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // OR Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = GamingBorder)
                Text(
                    text = "OR",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GamingTextMuted,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = GamingBorder)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Card 2: Join a Room
            GamingCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Link Icon Badge
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(GamingCyan, GamingNeonBlue)
                                )
                            )
                            .border(1.5.dp, GamingPrimaryPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Join a Room",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter a room code to join an existing room",
                        fontSize = 13.sp,
                        color = GamingTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Room Code Input with Paste Option
                    OutlinedTextField(
                        value = joinRoomCodeInput,
                        onValueChange = {
                            joinRoomCodeInput = it
                            joinCodeError = null
                        },
                        placeholder = { Text("Enter Room Code", color = GamingTextMuted, fontSize = 14.sp) },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                    if (clipText.isNotBlank()) {
                                        joinRoomCodeInput = clipText.trim()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste Code",
                                    tint = GamingCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GamingSecondaryBg,
                            unfocusedContainerColor = GamingSecondaryBg,
                            focusedBorderColor = GamingCyan,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = GamingTextPrimary,
                            unfocusedTextColor = GamingTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("room_code_input")
                    )

                    if (joinCodeError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = joinCodeError ?: "",
                            color = GamingErrorRed,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    GamingButton(
                        text = "Join Room",
                        onClick = {
                            val code = joinRoomCodeInput.trim()
                            if (code.isBlank()) {
                                joinCodeError = "Please enter a room code"
                            } else {
                                viewModel.prepareJoinRoom(code)
                            }
                        },
                        isLoading = uiState.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "submit_join_room_button"
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
