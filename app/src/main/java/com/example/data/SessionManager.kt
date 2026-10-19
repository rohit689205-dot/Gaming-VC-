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

    fun saveGuestUser(name: String = "") {
        val currentUid = userId
        val formattedName = if (name.isNotBlank()) name else "Guest (${currentUid.takeLast(4)})"
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
}
