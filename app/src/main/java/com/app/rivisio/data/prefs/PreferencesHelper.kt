package com.app.rivisio.data.prefs

interface PreferencesHelper {

    fun getAccessToken(): String?

    fun setAccessToken(accessToken: String)

    fun getUserEmail(): String?

    fun setUserEmail(email: String)

    fun getUserId(): Int

    fun setUserId(userId: Int)

    fun getName(): String?

    fun setName(name: String)

    fun getUserLoggedIn(): Boolean

    fun setUserLoggedIn()

    fun setUserLoggedOut()

    fun getUserState(): UserState?

    fun setUserState(userState: UserState)

    fun getGender(): String?

    fun setGender(gender: String)
}