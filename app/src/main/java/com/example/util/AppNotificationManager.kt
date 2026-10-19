package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.data.model.CallInvitation
import com.example.data.model.FriendRequest

object AppNotificationManager {

    const val CHANNEL_CALLS_ID = "channel_videocall_invitations"
    const val CHANNEL_FRIENDS_ID = "channel_friend_requests"

    const val ACTION_JOIN_ROOM = "com.example.ACTION_JOIN_ROOM"
    const val ACTION_DISMISS_INVITE = "com.example.ACTION_DISMISS_INVITE"
    const val ACTION_ACCEPT_FRIEND = "com.example.ACTION_ACCEPT_FRIEND"
    const val ACTION_DECLINE_FRIEND = "com.example.ACTION_DECLINE_FRIEND"

    const val EXTRA_ROOM_ID = "extra_room_id"
    const val EXTRA_INVITE_ID = "extra_invite_id"
    const val EXTRA_REQUEST_ID = "extra_request_id"
    const val EXTRA_FROM_USER_ID = "extra_from_user_id"
    const val EXTRA_FROM_USER_NAME = "extra_from_user_name"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // High Priority Channel for Video Call Invitations
            val callsChannel = NotificationChannel(
                CHANNEL_CALLS_ID,
                "Video Call Invitations",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Incoming video call alerts and invitations"
                enableVibration(true)
                setShowBadge(true)
            }

            // Default Priority Channel for Friend Requests
            val friendsChannel = NotificationChannel(
                CHANNEL_FRIENDS_ID,
                "Friend Requests",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts for new friend requests"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(callsChannel)
            notificationManager.createNotificationChannel(friendsChannel)
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun showCallInvitation(context: Context, invitation: CallInvitation) {
        if (!hasNotificationPermission(context)) return

        val notificationId = ("invite_" + invitation.id).hashCode()

        // Content intent: open app
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action intent: Join VC
        val joinIntent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_JOIN_ROOM
            putExtra(EXTRA_ROOM_ID, invitation.roomId)
            putExtra(EXTRA_INVITE_ID, invitation.id)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val joinPendingIntent = PendingIntent.getActivity(
            context,
            notificationId + 1,
            joinIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action intent: Dismiss
        val dismissIntent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_DISMISS_INVITE
            putExtra(EXTRA_INVITE_ID, invitation.id)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val dismissPendingIntent = PendingIntent.getActivity(
            context,
            notificationId + 2,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_CALLS_ID)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("Incoming Video Call")
            .setContentText("${invitation.fromUserName} invited you to Video Call (${invitation.roomId})")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setColor(0xFF6366F1.toInt())
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_call,
                "Join VC",
                joinPendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Decline",
                dismissPendingIntent
            )

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled safely
        }
    }

    fun showFriendRequest(context: Context, request: FriendRequest) {
        if (!hasNotificationPermission(context)) return

        val notificationId = ("friend_" + request.id).hashCode()

        // Content intent: open app
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action intent: Accept
        val acceptIntent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_ACCEPT_FRIEND
            putExtra(EXTRA_REQUEST_ID, request.id)
            putExtra(EXTRA_FROM_USER_ID, request.fromUserId)
            putExtra(EXTRA_FROM_USER_NAME, request.fromUserName)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val acceptPendingIntent = PendingIntent.getActivity(
            context,
            notificationId + 1,
            acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action intent: Decline
        val declineIntent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_DECLINE_FRIEND
            putExtra(EXTRA_REQUEST_ID, request.id)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val declinePendingIntent = PendingIntent.getActivity(
            context,
            notificationId + 2,
            declineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_FRIENDS_ID)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("New Friend Request")
            .setContentText("${request.fromUserName} sent you a friend request")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setColor(0xFF06B6D4.toInt())
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.checkbox_on_background,
                "Accept",
                acceptPendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Decline",
                declinePendingIntent
            )

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled safely
        }
    }

    fun cancelCallInvitation(context: Context, inviteId: String) {
        val notificationId = ("invite_$inviteId").hashCode()
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun cancelFriendRequest(context: Context, requestId: String) {
        val notificationId = ("friend_$requestId").hashCode()
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}
