package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallInvitation
import com.example.data.model.FriendRequest
import com.example.ui.AppScreen
import com.example.ui.theme.ActiveGreen
import com.example.ui.theme.CallRed
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingButtonGradient
import com.example.ui.theme.GamingCardBg
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingDarkBg
import com.example.ui.theme.GamingDeepPurple
import com.example.ui.theme.GamingElevatedSurface
import com.example.ui.theme.GamingErrorRed
import com.example.ui.theme.GamingNeonBlue
import com.example.ui.theme.GamingPrimaryPurple
import com.example.ui.theme.GamingSecondaryBg
import com.example.ui.theme.GamingSuccessGreen
import com.example.ui.theme.GamingTextMuted
import com.example.ui.theme.GamingTextPrimary
import com.example.ui.theme.GamingTextSecondary

/**
 * High-tech Gaming VC Logo Symbol: Controller + Headset with neon cyan & electric purple gradients
 */
@Composable
fun GamingLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 90.dp,
    showTagline: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing Futuristic Controller & Headset Canvas
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GamingPrimaryPurple.copy(alpha = 0.35f),
                            GamingSecondaryBg,
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(iconSize * 0.85f)) {
                val w = size.width
                val h = size.height

                // Headset Band Arch
                val bandPath = Path().apply {
                    moveTo(w * 0.22f, h * 0.52f)
                    cubicTo(
                        w * 0.22f, h * 0.18f,
                        w * 0.78f, h * 0.18f,
                        w * 0.78f, h * 0.52f
                    )
                }
                drawPath(
                    path = bandPath,
                    brush = Brush.horizontalGradient(
                        listOf(GamingCyan, GamingPrimaryPurple, GamingDeepPurple)
                    ),
                    style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
                )

                // Headset Earcups
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(GamingCyan, GamingNeonBlue)),
                    topLeft = Offset(w * 0.14f, h * 0.44f),
                    size = androidx.compose.ui.geometry.Size(w * 0.14f, h * 0.24f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.05f)
                )
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(GamingDeepPurple, GamingPrimaryPurple)),
                    topLeft = Offset(w * 0.72f, h * 0.44f),
                    size = androidx.compose.ui.geometry.Size(w * 0.14f, h * 0.24f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.05f)
                )

                // Gamepad Controller Body
                val controllerPath = Path().apply {
                    moveTo(w * 0.30f, h * 0.46f)
                    lineTo(w * 0.70f, h * 0.46f)
                    cubicTo(w * 0.84f, h * 0.46f, w * 0.88f, h * 0.62f, w * 0.82f, h * 0.78f)
                    lineTo(w * 0.76f, h * 0.86f)
                    cubicTo(w * 0.72f, h * 0.90f, w * 0.65f, h * 0.90f, w * 0.62f, h * 0.82f)
                    lineTo(w * 0.57f, h * 0.72f)
                    lineTo(w * 0.43f, h * 0.72f)
                    lineTo(w * 0.38f, h * 0.82f)
                    cubicTo(w * 0.35f, h * 0.90f, w * 0.28f, h * 0.90f, w * 0.24f, h * 0.86f)
                    lineTo(w * 0.18f, h * 0.78f)
                    cubicTo(w * 0.12f, h * 0.62f, w * 0.16f, h * 0.46f, w * 0.30f, h * 0.46f)
                    close()
                }
                drawPath(
                    path = controllerPath,
                    brush = Brush.linearGradient(
                        listOf(GamingPrimaryPurple, GamingNeonBlue, GamingCyan)
                    )
                )

                // Controller D-Pad Cross
                val dpadCenterX = w * 0.34f
                val dpadCenterY = h * 0.60f
                val arm = w * 0.035f
                val halfThick = w * 0.015f
                drawRect(
                    color = GamingTextPrimary,
                    topLeft = Offset(dpadCenterX - halfThick, dpadCenterY - arm),
                    size = androidx.compose.ui.geometry.Size(halfThick * 2, arm * 2)
                )
                drawRect(
                    color = GamingTextPrimary,
                    topLeft = Offset(dpadCenterX - arm, dpadCenterY - halfThick),
                    size = androidx.compose.ui.geometry.Size(arm * 2, halfThick * 2)
                )

                // Action Buttons
                val btnCenterX = w * 0.66f
                val btnCenterY = h * 0.60f
                val btnDist = w * 0.038f
                val btnRadius = w * 0.018f
                drawCircle(color = GamingCyan, radius = btnRadius, center = Offset(btnCenterX, btnCenterY - btnDist))
                drawCircle(color = GamingSuccessGreen, radius = btnRadius, center = Offset(btnCenterX + btnDist, btnCenterY))
                drawCircle(color = GamingErrorRed, radius = btnRadius, center = Offset(btnCenterX, btnCenterY + btnDist))
                drawCircle(color = Color(0xFFFFB800), radius = btnRadius, center = Offset(btnCenterX - btnDist, btnCenterY))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Brand Title with Gradient Text Styling
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Gaming ",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GamingTextPrimary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "VC",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GamingCyan,
                letterSpacing = 0.5.sp
            )
        }

        if (showTagline) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Play  •  Talk  •  Connect",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = GamingTextSecondary,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Join voice & video calls with your gaming squad",
                fontSize = 12.sp,
                color = GamingTextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Gaming Bottom Navigation Bar with 4 tabs:
 * 1. Home
 * 2. Rooms
 * 3. Friends
 * 4. Profile
 */
@Composable
fun GamingBottomBar(
    currentTab: AppScreen,
    onTabSelected: (AppScreen) -> Unit,
    unreadNotificationsCount: Int = 0,
    unreadFriendsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = GamingCardBg,
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = GamingBorder.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(GamingCardBg)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GamingBottomNavItem(
                    label = "Home",
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    isSelected = currentTab == AppScreen.DASHBOARD || currentTab == AppScreen.WELCOME,
                    badgeCount = 0,
                    onClick = { onTabSelected(AppScreen.DASHBOARD) },
                    testTag = "bottom_nav_home"
                )

                GamingBottomNavItem(
                    label = "Rooms",
                    icon = Icons.Outlined.Explore,
                    selectedIcon = Icons.Filled.Explore,
                    isSelected = currentTab == AppScreen.ROOM_DISCOVERY || currentTab == AppScreen.CREATE_JOIN_ROOM,
                    badgeCount = 0,
                    onClick = { onTabSelected(AppScreen.ROOM_DISCOVERY) },
                    testTag = "bottom_nav_rooms"
                )

                GamingBottomNavItem(
                    label = "Friends",
                    icon = Icons.Outlined.People,
                    selectedIcon = Icons.Filled.People,
                    isSelected = currentTab == AppScreen.FRIENDS,
                    badgeCount = unreadFriendsCount,
                    onClick = { onTabSelected(AppScreen.FRIENDS) },
                    testTag = "bottom_nav_friends"
                )

                GamingBottomNavItem(
                    label = "Profile",
                    icon = Icons.Outlined.Person,
                    selectedIcon = Icons.Filled.Person,
                    isSelected = currentTab == AppScreen.PROFILE || currentTab == AppScreen.SETTINGS,
                    badgeCount = 0,
                    onClick = { onTabSelected(AppScreen.PROFILE) },
                    testTag = "bottom_nav_profile"
                )
            }
        }
    }
}

@Composable
private fun GamingBottomNavItem(
    label: String,
    icon: ImageVector,
    selectedIcon: ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = GamingErrorRed,
                        contentColor = Color.White
                    ) {
                        Text(text = if (badgeCount > 99) "99+" else badgeCount.toString(), fontSize = 10.sp)
                    }
                }
            }
        ) {
            Icon(
                imageVector = if (isSelected) selectedIcon else icon,
                contentDescription = label,
                tint = if (isSelected) GamingCyan else GamingTextMuted,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) GamingCyan else GamingTextMuted
        )

        // Active Indicator Glow Line
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .width(16.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(if (isSelected) GamingCyan else Color.Transparent)
        )
    }
}

/**
 * Premium Gradient Action Button
 */
@Composable
fun GamingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isDestructive: Boolean = false,
    isSecondary: Boolean = false,
    testTag: String = "gaming_button"
) {
    val shape = RoundedCornerShape(16.dp)

    if (isSecondary) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .height(50.dp)
                .testTag(testTag),
            enabled = enabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = GamingElevatedSurface.copy(alpha = 0.5f),
                contentColor = GamingTextPrimary
            ),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, GamingBorder)
        ) {
            ButtonContent(text, icon, isLoading)
        }
    } else if (isDestructive) {
        Button(
            onClick = onClick,
            modifier = modifier
                .height(50.dp)
                .testTag(testTag),
            enabled = enabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = GamingErrorRed,
                contentColor = Color.White
            )
        ) {
            ButtonContent(text, icon, isLoading)
        }
    } else {
        // Primary Gradient Button
        Box(
            modifier = modifier
                .height(50.dp)
                .clip(shape)
                .background(if (enabled) GamingButtonGradient else Brush.linearGradient(listOf(GamingBorder, GamingBorder)))
                .clickable(enabled = enabled && !isLoading) { onClick() }
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ButtonContent(text, icon, isLoading)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector?,
    isLoading: Boolean
) {
    if (isLoading) {
        CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
        )
    } else {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * Standard Gaming Card container with border and dark cyberpunk depth
 */
@Composable
fun GamingCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = GamingCardBg
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
    ) {
        content()
    }
}

/**
 * Game Category Pill Badge
 */
@Composable
fun GameBadge(
    game: String,
    modifier: Modifier = Modifier
) {
    val (badgeBg, badgeText) = when (game.lowercase()) {
        "bgmi" -> Color(0xFFF97316).copy(alpha = 0.2f) to Color(0xFFFDBA74)
        "valorant" -> Color(0xFFFF4655).copy(alpha = 0.2f) to Color(0xFFFDA4AF)
        "free fire" -> Color(0xFFF59E0B).copy(alpha = 0.2f) to Color(0xFFFCD34D)
        "minecraft" -> Color(0xFF10B981).copy(alpha = 0.2f) to Color(0xFF6EE7B7)
        else -> GamingCyan.copy(alpha = 0.2f) to GamingCyan
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(badgeBg)
            .padding(horizontal = 7.dp, vertical = 2.5.dp)
    ) {
        Text(
            text = game,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = badgeText
        )
    }
}

/**
 * Gamer Avatar with online status glow
 */
@Composable
fun GamerAvatar(
    name: String,
    size: Dp = 44.dp,
    isOnline: Boolean = true,
    role: String? = null,
    modifier: Modifier = Modifier
) {
    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifBlank { "G" }

    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(GamingPrimaryPurple, GamingNeonBlue)
                    )
                )
                .border(1.5.dp, GamingBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = (size.value * 0.38f).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(size * 0.30f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(GamingSuccessGreen)
                    .border(2.dp, GamingDarkBg, CircleShape)
            )
        }
    }
}

/**
 * Notifications and Invitations Modal Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsBottomSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    incomingInvitations: List<CallInvitation>,
    incomingFriendRequests: List<FriendRequest>,
    onJoinCall: (CallInvitation) -> Unit,
    onDeclineCall: (String) -> Unit,
    onAcceptFriend: (FriendRequest) -> Unit,
    onDeclineFriend: (String) -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GamingSecondaryBg,
        tonalElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = GamingCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notifications & Invites",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingTextPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = GamingTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Incoming Call Invitations
            if (incomingInvitations.isNotEmpty()) {
                Text(
                    text = "INCOMING CALL INVITATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GamingCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                incomingInvitations.forEach { invite ->
                    GamingCard(modifier = Modifier.padding(bottom = 10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = invite.roomTitle.ifBlank { "Voice Room" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingTextPrimary
                                )
                                Text(
                                    text = "${invite.fromUserName} invited you to join",
                                    fontSize = 12.sp,
                                    color = GamingTextSecondary
                                )
                            }

                            Row {
                                OutlinedButton(
                                    onClick = { onDeclineCall(invite.id) },
                                    modifier = Modifier.height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GamingErrorRed),
                                    contentPadding = PaddingValues(horizontal = 10.dp)
                                ) {
                                    Text("Decline", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { onJoinCall(invite) },
                                    modifier = Modifier.height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingPrimaryPurple),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Text("Join VC", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Incoming Friend Requests
            if (incomingFriendRequests.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "FRIEND REQUESTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GamingPrimaryPurple,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                incomingFriendRequests.forEach { req ->
                    GamingCard(modifier = Modifier.padding(bottom = 10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                GamerAvatar(name = req.fromUserName, size = 38.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = req.fromUserName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GamingTextPrimary
                                    )
                                    Text(
                                        text = "Wants to connect as gaming friends",
                                        fontSize = 11.sp,
                                        color = GamingTextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row {
                                OutlinedButton(
                                    onClick = { onDeclineFriend(req.id) },
                                    modifier = Modifier.height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GamingTextMuted),
                                    contentPadding = PaddingValues(horizontal = 10.dp)
                                ) {
                                    Text("Decline", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { onAcceptFriend(req) },
                                    modifier = Modifier.height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingSuccessGreen),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Text("Accept", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }

            if (incomingInvitations.isEmpty() && incomingFriendRequests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = GamingTextMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "All caught up!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No pending calls or friend requests",
                            fontSize = 13.sp,
                            color = GamingTextMuted
                        )
                    }
                }
            }
        }
    }
}
