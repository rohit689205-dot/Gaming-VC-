package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.CallViewModel
import com.example.ui.components.GamingBottomBar
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingCardBg
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingDarkBg
import com.example.ui.theme.GamingElevatedSurface
import com.example.ui.theme.GamingPrimaryPurple
import com.example.ui.theme.GamingSecondaryBg
import com.example.ui.theme.GamingTextMuted
import com.example.ui.theme.GamingTextPrimary
import com.example.ui.theme.GamingTextSecondary

@Composable
fun RoomDiscoveryScreen(
    viewModel: CallViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler {
        viewModel.selectBottomTab(AppScreen.DASHBOARD)
    }

    val categories = listOf("All", "BGMI", "Valorant", "Free Fire", "Minecraft", "General")

    val allRooms = (uiState.discoveredRooms + uiState.recentRooms).distinctBy { it.id }

    val filteredRooms = allRooms.filter { room ->
        val notTutorial = !room.title.contains("tutorial", ignoreCase = true) &&
                !room.category.contains("tutorial", ignoreCase = true) &&
                !room.description.contains("tutorial", ignoreCase = true) &&
                !room.id.contains("tutorial", ignoreCase = true)
        val matchesCategory = uiState.selectedCategory == "All" || room.category.equals(uiState.selectedCategory, ignoreCase = true)
        val matchesSearch = uiState.searchQuery.isBlank() ||
                room.title.contains(uiState.searchQuery, ignoreCase = true) ||
                room.category.contains(uiState.searchQuery, ignoreCase = true)
        notTutorial && matchesCategory && matchesSearch
    }

    Scaffold(
        bottomBar = {
            GamingBottomBar(
                currentTab = AppScreen.ROOM_DISCOVERY,
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
                .padding(horizontal = 16.dp)
        ) {
            // Header: Back button + Voice Rooms + Filter icon
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
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Voice Rooms",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingTextPrimary
                    )
                }

                IconButton(
                    onClick = { /* Filter sort action */ },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingCardBg)
                        .border(1.dp, GamingBorder, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = GamingCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search rooms...", color = GamingTextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = GamingCyan, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = GamingTextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                },
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
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("room_search_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Chips (Horizontal Scroll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = uiState.selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GamingPrimaryPurple else GamingElevatedSurface)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) GamingCyan else GamingBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setSelectedCategory(category) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else GamingTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rooms List
            if (filteredRooms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
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
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No Rooms Available",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No active rooms found. Launch your own room or join using a room code!",
                            fontSize = 13.sp,
                            color = GamingTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.CREATE_JOIN_ROOM) },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingPrimaryPurple),
                            modifier = Modifier.testTag("rooms_empty_state_create_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Room", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredRooms) { room ->
                        GamingRoomCard(
                            room = room,
                            onJoin = { viewModel.prepareJoinRoom(room.id) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}
