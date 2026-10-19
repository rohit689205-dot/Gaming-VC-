package com.example.data

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("videocall_user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_IS_GUEST_MODE = "key_is_guest_mode"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_DISPLAY_NAME = "key_display_name"
        private const val KEY_EMAIL = "key_email"
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var isGuestMode: Boolean
        get() = prefs.getBoolean(KEY_IS_GUEST_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_GUEST_MODE, value).apply()

    var userId: String
        get() {
            var uid = prefs.getString(KEY_USER_ID, null)
            if (uid.isNullOrBlank()) {
                val shortId = (100000..999999).random().toString()
                uid = "guest_$shortId"
                prefs.edit().putString(KEY_USER_ID, uid).apply()
            }
            return uid
        }
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var displayName: String
        get() = prefs.getString(KEY_DISPLAY_NAME, "Guest User") ?: "Guest User"
        set(value) = prefs.edit().putString(KEY_DISPLAY_NAME, value).apply()

    var email: String
        get() = prefs.getString(KEY_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    fun saveGoogleUser(uid: String, name: String, userEmail: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putBoolean(KEY_IS_GUEST_MODE, false)
            .putString(KEY_USER_ID, uid)
            .putString(KEY_DISPLAY_NAME, name.ifBlank { "User" })
            .putString(KEY_EMAIL, userEmail)
            .apply()
    }

    fun generateNewGuestUid(): String {
        val shortId = (100000..999999).random().toString()
        val newUid = "guest_$shortId"
        userId = newUid
        return newUid
    }

    fun saveGuestUser(name: String = "", customUid: String? = null) {
        if (!customUid.isNullOrBlank()) {
            userId = customUid.trim()
        }
        val currentUid = userId
        val formattedName = if (name.isNotBlank()) name.trim() else "Guest (${currentUid.takeLast(4)})"
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putBoolean(KEY_IS_GUEST_MODE, true)
            .putString(KEY_DISPLAY_NAME, formattedName)
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .putBoolean(KEY_IS_GUEST_MODE, false)
            .remove(KEY_DISPLAY_NAME)
            .remove(KEY_EMAIL)
            .apply()
    }

    fun getRecentRooms(): List<com.example.data.model.RoomSession> {
        val jsonStr = prefs.getString("key_recent_rooms", null)
        if (jsonStr.isNullOrBlank()) {
            return emptyList()
        }
        return try {
            val jsonArray = org.json.JSONArray(jsonStr)
            val list = mutableListOf<com.example.data.model.RoomSession>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    com.example.data.model.RoomSession(
                        id = obj.optString("id"),
                        hostId = obj.optString("hostId"),
                        hostName = obj.optString("hostName"),
                        title = obj.optString("title"),
                        category = obj.optString("category", "General"),
                        description = obj.optString("description"),
                        maxParticipants = obj.optInt("maxParticipants", 12),
                        mode = obj.optString("mode", "Voice"),
                        coverTag = obj.optString("coverTag", "general"),
                        status = obj.optString("status", "active")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun clearRecentRooms() {
        prefs.edit().remove("key_recent_rooms").apply()
    }

    fun saveRecentRoom(room: com.example.data.model.RoomSession): List<com.example.data.model.RoomSession> {
        val current = getRecentRooms().toMutableList()
        current.removeAll { it.id == room.id }
        current.add(0, room)
        val trimmed = current.take(10)
        try {
            val jsonArray = org.json.JSONArray()
            for (item in trimmed) {
                val obj = org.json.JSONObject()
                obj.put("id", item.id)
                obj.put("hostId", item.hostId)
                obj.put("hostName", item.hostName)
                obj.put("title", item.title)
                obj.put("category", item.category)
                obj.put("description", item.description)
                obj.put("maxParticipants", item.maxParticipants)
                obj.put("mode", item.mode)
                obj.put("coverTag", item.coverTag)
                obj.put("status", item.status)
                jsonArray.put(obj)
            }
            prefs.edit().putString("key_recent_rooms", jsonArray.toString()).apply()
        } catch (_: Exception) {}
        return trimmed
    }
}
