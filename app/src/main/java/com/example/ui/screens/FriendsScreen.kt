package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FriendItem
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
import com.example.ui.theme.GamingElevatedSurface
import com.example.ui.theme.GamingErrorRed
import com.example.ui.theme.GamingPrimaryPurple
import com.example.ui.theme.GamingSecondaryBg
import com.example.ui.theme.GamingSuccessGreen
import com.example.ui.theme.GamingTextMuted
import com.example.ui.theme.GamingTextPrimary
import com.example.ui.theme.GamingTextSecondary

@Composable
fun FriendsScreen(
    viewModel: CallViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val myUid = uiState.myUserId.ifBlank { "gamer-squad-uid-99" }

    // Filter real friends and remove tutorial/bot friends
    val friendList = uiState.friends.filter { friend ->
        !friend.friendName.contains("tutorial", ignoreCase = true) &&
        !friend.friendUserId.contains("tutorial", ignoreCase = true) &&
        !friend.id.contains("tutorial", ignoreCase = true)
    }

    BackHandler {
        viewModel.selectBottomTab(AppScreen.DASHBOARD)
    }

    Scaffold(
        bottomBar = {
            GamingBottomBar(
                currentTab = AppScreen.FRIENDS,
                onTabSelected = { tab -> viewModel.selectBottomTab(tab) },
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
                contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
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
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gaming Squad & Friends",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingTextPrimary
                            )
                        }
                    }
                }

                // Your Gamer UID Card (with 1-tap Copy)
                item {
                    GamingCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GamingSecondaryBg)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "YOUR GAMER UID",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingCyan,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = myUid,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingTextPrimary
                                )
                                Text(
                                    text = "Share this UID with friends to receive invites",
                                    fontSize = 11.sp,
                                    color = GamingTextSecondary
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("My Gamer UID", myUid))
                                    Toast.makeText(context, "UID copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GamingPrimaryPurple)
                                    .testTag("copy_my_uid_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy UID",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Add Friend by UID Field
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Add Friend by UID",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.friendUidInput,
                            onValueChange = { viewModel.setFriendUidInput(it) },
                            placeholder = { Text("Paste Friend's UID...", color = GamingTextMuted, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = GamingCardBg,
                                unfocusedContainerColor = GamingCardBg,
                                focusedBorderColor = GamingCyan,
                                unfocusedBorderColor = GamingBorder,
                                focusedTextColor = GamingTextPrimary,
                                unfocusedTextColor = GamingTextPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("friend_uid_input")
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                viewModel.sendFriendRequest(uiState.friendUidInput)
                            },
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("send_friend_request_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GamingPrimaryPurple)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Pending Incoming Friend Requests
                if (uiState.incomingFriendRequests.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(22.dp))
                        Text(
                            text = "Pending Friend Requests (${uiState.incomingFriendRequests.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingCyan
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(uiState.incomingFriendRequests) { req ->
                        GamingCard(modifier = Modifier.padding(bottom = 8.dp)) {
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
                                    GamerAvatar(name = req.fromUserName, size = 42.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = req.fromUserName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GamingTextPrimary
                                        )
                                        Text(
                                            text = "Wants to be friends",
                                            fontSize = 11.sp,
                                            color = GamingTextSecondary
                                        )
                                    }
                                }

                                Row {
                                    OutlinedButton(
                                        onClick = { viewModel.declineFriendRequest(req.id) },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(36.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GamingTextMuted)
                                    ) {
                                        Text("Decline", fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Button(
                                        onClick = { viewModel.acceptFriendRequest(req) },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(36.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingSuccessGreen)
                                    ) {
                                        Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }

                // Online Friends List
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "My Friends (${friendList.size})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (friendList.isEmpty()) {
                    item {
                        GamingCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(GamingCardBg)
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = GamingCyan,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No Real Friends Added Yet",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Paste a friend's real UID above to send a request, or share your UID so friends can add you!",
                                    fontSize = 12.sp,
                                    color = GamingTextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Gamer UID", myUid))
                                        Toast.makeText(context, "My UID copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingSecondaryBg),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = GamingCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy My UID", fontSize = 12.sp, color = GamingCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(friendList) { friend ->
                        GamingCard(modifier = Modifier.padding(bottom = 8.dp)) {
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
                                    GamerAvatar(
                                        name = friend.friendName,
                                        size = 44.dp,
                                        isOnline = friend.isOnline
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = friend.friendName,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GamingTextPrimary
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Friend UID", friend.friendUserId))
                                                Toast.makeText(context, "Copied UID: ${friend.friendUserId}", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Text(
                                                text = "ID: ${friend.friendUserId}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = GamingCyan
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy UID",
                                                tint = GamingCyan,
                                                modifier = Modifier.size(11.dp)
                                            )
                                        }
                                        Text(
                                            text = if (friend.isOnline) "In Lobby • Active" else "Offline",
                                            fontSize = 10.sp,
                                            color = if (friend.isOnline) GamingSuccessGreen else GamingTextMuted
                                        )
                                    }
                                }

                                // Voice Call / Invite Button
                                Button(
                                    onClick = {
                                        viewModel.inviteFriendToCurrentCall(friend)
                                        Toast.makeText(context, "Call invitation sent to ${friend.friendName}!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(38.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingPrimaryPurple)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
