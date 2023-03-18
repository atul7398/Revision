package com.app.rivisio.data.prefs

import dagger.hilt.android.qualifiers.ApplicationContext

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import javax.inject.Inject

class PreferencesHelperImpl @Inject constructor(@ApplicationContext context: Context) :
    PreferencesHelper {

    private val PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"
    private val PREF_KEY_USER_EMAIL = "PREF_KEY_USER_EMAIL"
    private val PREF_KEY_USER_ID = "PREF_KEY_USER_ID"
    private val PREF_KEY_USER_NAME = "PREF_KEY_USER_NAME"
    private val PREF_KEY_USER_GENDER = "PREF_KEY_USER_GENDER"
    private val PREF_KEY_USER_LOGGED_IN = "PREF_KEY_USER_LOGGED_IN"
    private val PREF_KEY_USER_STATE = "PREF_KEY_USER_STATE"

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)


    override fun getAccessToken(): String? {
        return sharedPreferences.getString(PREF_KEY_ACCESS_TOKEN, null)
    }

    override fun setAccessToken(accessToken: String) {
        sharedPreferences.edit().putString(PREF_KEY_ACCESS_TOKEN, accessToken).apply()
    }

    override fun getUserEmail(): String? {
        return sharedPreferences.getString(PREF_KEY_USER_EMAIL, null)
    }

    override fun setUserEmail(email: String) {
        sharedPreferences.edit().putString(PREF_KEY_USER_EMAIL, email).apply()
    }

    override fun getUserId(): Int {
        return sharedPreferences.getInt(PREF_KEY_USER_ID, -1)
    }

    override fun setUserId(userId: Int) {
        sharedPreferences.edit().putInt(PREF_KEY_USER_ID, userId).apply()
    }

    override fun getName(): String? {
        return sharedPreferences.getString(PREF_KEY_USER_NAME, null)
    }

    override fun setName(name: String) {
        sharedPreferences.edit().putString(PREF_KEY_USER_NAME, name).apply()
    }

    override fun getGender(): String? {
        return sharedPreferences.getString(PREF_KEY_USER_GENDER, null)
    }

    override fun setGender(gender: String) {
        sharedPreferences.edit().putString(PREF_KEY_USER_GENDER, gender).apply()
    }

    override fun getUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(PREF_KEY_USER_LOGGED_IN, false)
    }

    override fun setUserLoggedIn() {
        sharedPreferences.edit().putBoolean(PREF_KEY_USER_LOGGED_IN, true).apply()
    }

    override fun setUserLoggedOut() {
        sharedPreferences.edit {
            remove(PREF_KEY_ACCESS_TOKEN)
            remove(PREF_KEY_USER_EMAIL)
            remove(PREF_KEY_USER_ID)
            remove(PREF_KEY_USER_NAME)
            remove(PREF_KEY_USER_GENDER)
            remove(PREF_KEY_USER_LOGGED_IN)
            remove(PREF_KEY_USER_STATE)
        }
    }

    override fun getUserState(): UserState? {
        return UserState.getByValue(
            sharedPreferences.getInt(
                PREF_KEY_USER_STATE,
                UserState.EMPTY.value
            )
        )
    }

    override fun setUserState(userState: UserState) {
        sharedPreferences.edit().putInt(PREF_KEY_USER_STATE, userState.value).apply()
    }

}