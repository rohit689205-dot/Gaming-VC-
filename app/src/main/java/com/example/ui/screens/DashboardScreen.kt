package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.GamingErrorRed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoomSession
import com.example.ui.AppScreen
import com.example.ui.CallViewModel
import com.example.ui.components.GameBadge
import com.example.ui.components.GamerAvatar
import com.example.ui.components.GamingBottomBar
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.NotificationsBottomSheet
import com.example.ui.theme.ActiveGreen
import com.example.ui.theme.CallRed
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
fun DashboardScreen(
    viewModel: CallViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val unreadNotificationsCount = uiState.incomingCallInvitations.size + uiState.incomingFriendRequests.size

    Scaffold(
        bottomBar = {
            GamingBottomBar(
                currentTab = AppScreen.DASHBOARD,
                onTabSelected = { tab -> viewModel.selectBottomTab(tab) },
                unreadNotificationsCount = uiState.incomingCallInvitations.size,
                unreadFriendsCount = uiState.incomingFriendRequests.size
            )
        },
        containerColor = GamingDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GamingDarkBg)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
            ) {
                // Top Header: Avatar + Brand + Notifications Icon
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Avatar with Online Green Dot
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.selectBottomTab(AppScreen.PROFILE) }
                        ) {
                            GamerAvatar(
                                name = uiState.myDisplayName.ifBlank { "Raj Kumari" },
                                size = 46.dp,
                                isOnline = true
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Gaming ",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GamingTextPrimary
                                    )
                                    Text(
                                        text = "VC",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GamingCyan
                                    )
                                }
                                Text(
                                    text = if (uiState.isGuestMode) "UID: ${uiState.myUserId}" else uiState.userHandle.ifBlank { "@rajkumari" },
                                    fontSize = 12.sp,
                                    fontWeight = if (uiState.isGuestMode) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (uiState.isGuestMode) GamingCyan else GamingTextSecondary
                                )
                            }
                        }

                        // Right: Notification Bell with Badge
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(
                                        containerColor = GamingErrorRed,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = unreadNotificationsCount.toString(), fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleNotificationsSheet() },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GamingCardBg)
                                    .border(1.dp, GamingBorder, CircleShape)
                                    .testTag("notifications_bell_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = if (unreadNotificationsCount > 0) GamingCyan else GamingTextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // In-App Friend Notice / Banner
                if (uiState.friendNotice != null) {
                    item {
                        GamingCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(GamingPrimaryPurple.copy(alpha = 0.2f))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.friendNotice ?: "",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GamingTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.dismissFriendNotice() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = GamingTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Hero Banner: "Good Vibes, Great Games +"
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    HeroGamingBanner(
                        onExploreClick = { viewModel.selectBottomTab(AppScreen.ROOM_DISCOVERY) }
                    )
                }

                // Quick Actions (4 compact icon buttons): Join Room, Create Room, Friends, Settings
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Quick Actions",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionItem(
                            icon = Icons.Default.Mic,
                            label = "Join Room",
                            onClick = { viewModel.navigateTo(AppScreen.CREATE_JOIN_ROOM) },
                            testTag = "quick_action_join"
                        )
                        QuickActionItem(
                            icon = Icons.Default.Add,
                            label = "Create Room",
                            onClick = { viewModel.navigateTo(AppScreen.CREATE_JOIN_ROOM) },
                            testTag = "quick_action_create"
                        )
                        QuickActionItem(
                            icon = Icons.Default.People,
                            label = "Friends",
                            badgeCount = uiState.incomingFriendRequests.size,
                            onClick = { viewModel.selectBottomTab(AppScreen.FRIENDS) },
                            testTag = "quick_action_friends"
                        )
                        QuickActionItem(
                            icon = Icons.Default.Settings,
                            label = "Settings",
                            onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                            testTag = "quick_action_settings"
                        )
                    }
                }

                // Recently Joined Rooms Section Header
                item {
                    Spacer(modifier = Modifier.height(26.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recently Joined Rooms",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingTextPrimary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { viewModel.selectBottomTab(AppScreen.ROOM_DISCOVERY) }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "View All",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GamingCyan
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = GamingCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Recently Joined Rooms List or Empty State
                if (uiState.recentRooms.isEmpty()) {
                    item {
                        GamingCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(GamingCardBg)
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(GamingSecondaryBg)
                                        .border(1.dp, GamingBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = GamingCyan,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Recently Joined Rooms",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Jump into squad action! Create your own room or explore active gaming rooms.",
                                    fontSize = 12.sp,
                                    color = GamingTextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.navigateTo(AppScreen.CREATE_JOIN_ROOM) },
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingPrimaryPurple),
                                        modifier = Modifier.testTag("create_room_empty_state_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Create Room", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { viewModel.selectBottomTab(AppScreen.ROOM_DISCOVERY) },
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingSecondaryBg),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder),
                                        modifier = Modifier.testTag("explore_rooms_empty_state_button")
                                    ) {
                                        Text("Explore", fontSize = 13.sp, color = GamingCyan, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(uiState.recentRooms) { room ->
                        GamingRoomCard(
                            room = room,
                            onJoin = {
                                viewModel.prepareJoinRoom(room.id)
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }

    // Interactive Notifications & Invitations Bottom Sheet
    NotificationsBottomSheet(
        isOpen = uiState.isNotificationsSheetOpen,
        onDismiss = { viewModel.toggleNotificationsSheet() },
        incomingInvitations = uiState.incomingCallInvitations,
        incomingFriendRequests = uiState.incomingFriendRequests,
        onJoinCall = { invite ->
            viewModel.toggleNotificationsSheet()
            viewModel.joinFromCallInvitation(invite)
        },
        onDeclineCall = { id -> viewModel.dismissCallInvitation(id) },
        onAcceptFriend = { req -> viewModel.acceptFriendRequest(req) },
        onDeclineFriend = { id -> viewModel.declineFriendRequest(id) }
    )
}

/**
 * Hero Banner with cybernetic controller graphics, gradient background, and Explore button
 */
@Composable
fun HeroGamingBanner(
    onExploreClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GamingHeroGradient)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Neon Controller & Headset Graphics drawn with Canvas on the right side
            Canvas(
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.CenterEnd)
            ) {
                val w = size.width
                val h = size.height

                // Cybernetic glowing headset band
                val band = Path().apply {
                    moveTo(w * 0.25f, h * 0.5f)
                    cubicTo(w * 0.25f, h * 0.15f, w * 0.85f, h * 0.15f, w * 0.85f, h * 0.5f)
                }
                drawPath(
                    path = band,
                    brush = Brush.horizontalGradient(listOf(GamingCyan, GamingDeepPurple)),
                    style = Stroke(width = 8f)
                )

                // Wireless controller outline with cyan glow
                val pad = Path().apply {
                    moveTo(w * 0.35f, h * 0.45f)
                    lineTo(w * 0.8f, h * 0.45f)
                    cubicTo(w * 0.95f, h * 0.45f, w * 0.95f, h * 0.75f, w * 0.85f, h * 0.9f)
                    lineTo(w * 0.7f, h * 0.78f)
                    lineTo(w * 0.5f, h * 0.78f)
                    lineTo(w * 0.35f, h * 0.9f)
                    cubicTo(w * 0.25f, h * 0.75f, w * 0.25f, h * 0.45f, w * 0.35f, h * 0.45f)
                    close()
                }
                drawPath(
                    path = pad,
                    brush = Brush.linearGradient(listOf(GamingPrimaryPurple, GamingNeonBlue))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .align(Alignment.CenterStart),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Good Vibes\nGreat Games ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GamingTextPrimary,
                        lineHeight = 24.sp
                    )
                    Text(
                        text = "+",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = GamingCyan
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Join voice rooms, meet gamers and play together!",
                    fontSize = 11.sp,
                    color = GamingTextSecondary,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GamingButtonGradient)
                        .clickable { onExploreClick() }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Explore Rooms",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Compact Icon Quick Action Button
 */
@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    badgeCount: Int = 0,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = GamingErrorRed,
                        contentColor = Color.White
                    ) {
                        Text(text = badgeCount.toString(), fontSize = 9.sp)
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GamingCardBg)
                    .border(1.dp, GamingBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = GamingCyan,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = GamingTextSecondary
        )
    }
}

/**
 * Room Card with Game Icon, Category, Participant count, and Join Button
 */
@Composable
fun GamingRoomCard(
    room: RoomSession,
    onJoin: () -> Unit
) {
    GamingCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onJoin
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Game Thumbnail / Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (room.category.lowercase()) {
                                "bgmi" -> Brush.linearGradient(listOf(Color(0xFF7C2D12), Color(0xFFC2410C)))
                                "valorant" -> Brush.linearGradient(listOf(Color(0xFF881337), Color(0xFFE11D48)))
                                "free fire" -> Brush.linearGradient(listOf(Color(0xFF78350F), Color(0xFFD97706)))
                                "minecraft" -> Brush.linearGradient(listOf(Color(0xFF064E3B), Color(0xFF059669)))
                                else -> Brush.linearGradient(listOf(GamingPrimaryPurple, GamingNeonBlue))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = room.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GameBadge(game = room.category)

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(GamingSuccessGreen)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${room.maxParticipants} online",
                            fontSize = 11.sp,
                            color = GamingTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Small Participant Avatar Stack
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf("A", "R", "P").forEachIndexed { index, initial ->
                            Box(
                                modifier = Modifier
                                    .padding(start = if (index > 0) 3.dp else 0.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(GamingPrimaryPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(initial, fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Join Button
            Button(
                onClick = onJoin,
                modifier = Modifier
                    .height(36.dp)
                    .testTag("join_room_${room.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GamingPrimaryPurple,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = "Join",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
