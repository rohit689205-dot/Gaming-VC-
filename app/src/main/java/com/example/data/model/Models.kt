package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val username: String = "",
    val email: String = "",
    val bio: String = "Gaming enthusiast • Squad leader",
    val avatarUrl: String = "",
    val roomsJoined: Int = 12,
    val friendsCount: Int = 8,
    val gamesPlayed: Int = 3,
    val favoriteGames: List<String> = listOf("BGMI", "Valorant", "Free Fire", "Minecraft"),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class RoomSession(
    @DocumentId val id: String = "",
    val hostId: String = "",
    val hostName: String = "",
    val title: String = "",
    val category: String = "General", // BGMI, Valorant, Free Fire, Minecraft, General
    val description: String = "Hangout & voice chat with the squad",
    val isPrivate: Boolean = false,
    val maxParticipants: Int = 12,
    val mode: String = "Voice", // "Voice" or "Video"
    val coverTag: String = "general",
    val participantLimit: Int = 12,
    val status: String = "active", // "waiting", "active", "ended"
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class RoomParticipant(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val isAudioMuted: Boolean = false,
    val isVideoOff: Boolean = false,
    val isHost: Boolean = false,
    val role: String = "Member", // "Leader", "Member"
    @ServerTimestamp val joinedAt: Timestamp? = null,
    @ServerTimestamp val lastSeen: Timestamp? = null
)

@IgnoreExtraProperties
data class SignalMessage(
    @DocumentId val id: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val type: String = "heartbeat", // offer, answer, candidate, leave, heartbeat
    val payload: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ChatMessage(
    @DocumentId val id: String = "",
    val roomId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    @ServerTimestamp val timestamp: Timestamp? = null
)

@IgnoreExtraProperties
data class MeetingHistoryItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val roomId: String = "",
    val title: String = "",
    val category: String = "General",
    @ServerTimestamp val joinedAt: Timestamp? = null,
    val durationMinutes: Long = 0L
)

@IgnoreExtraProperties
data class FriendRequest(
    @DocumentId val id: String = "",
    val fromUserId: String = "",
    val fromUserName: String = "",
    val toUserId: String = "",
    val status: String = "pending", // "pending", "accepted", "declined"
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FriendItem(
    @DocumentId val id: String = "",
    val friendUserId: String = "",
    val friendName: String = "",
    val friendEmail: String = "",
    val isOnline: Boolean = true,
    @ServerTimestamp val addedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CallInvitation(
    @DocumentId val id: String = "",
    val fromUserId: String = "",
    val fromUserName: String = "",
    val toUserId: String = "",
    val roomId: String = "",
    val roomTitle: String = "",
    val category: String = "General",
    val status: String = "pending", // "pending", "accepted", "declined"
    @ServerTimestamp val createdAt: Timestamp? = null
)
