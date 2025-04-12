package com.example.dag.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class UserPreferences(private val context: Context? = null) {
    
    companion object {
        private const val PREFS_NAME = "dag_preferences"
        private const val KEY_PRIVACY_POLICY_AGREED = "privacy_policy_agreed"
        private const val KEY_LOGGED_IN = "logged_in"
    }
    
    private val sharedPreferences: SharedPreferences? by lazy {
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun hasAgreedToPrivacyPolicy(): Boolean {
        return sharedPreferences?.getBoolean(KEY_PRIVACY_POLICY_AGREED, false) ?: false
    }
    
    fun setPrivacyPolicyAgreed(agreed: Boolean) {
        sharedPreferences?.edit()?.putBoolean(KEY_PRIVACY_POLICY_AGREED, agreed)?.apply()
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences?.getBoolean(KEY_LOGGED_IN, false) ?: false
    }
    
    fun setLoggedIn(loggedIn: Boolean) {
        sharedPreferences?.edit()?.putBoolean(KEY_LOGGED_IN, loggedIn)?.apply()
    }
}

@Composable
fun rememberUserPreferences(): UserPreferences {
    val context = LocalContext.current
    return remember { UserPreferences(context) }
}