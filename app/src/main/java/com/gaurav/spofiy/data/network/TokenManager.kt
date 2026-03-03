package com.gaurav.spofiy.data.network

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String, expiresIn: Int) {
        val expirationTime = System.currentTimeMillis() + (expiresIn * 1000)
        prefs.edit()
            .putString("access_token", token)
            .putLong("expiration_time", expirationTime)
            .apply()
    }

    fun getToken(): String? {
        val token = prefs.getString("access_token", null)
        val expirationTime = prefs.getLong("expiration_time", 0)
        
        // If token is expired or not found, return null
        if (token == null || System.currentTimeMillis() > expirationTime) {
            return null
        }
        return token
    }
}
