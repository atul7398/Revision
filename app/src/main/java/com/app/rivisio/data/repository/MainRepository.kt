package com.app.rivisio.data.repository

import com.app.rivisio.data.network.ApiHelper
import com.app.rivisio.data.network.USER_ID
import com.app.rivisio.data.prefs.PreferencesHelper
import com.app.rivisio.data.prefs.UserState
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.http.Query
import javax.inject.Inject

@ViewModelScoped
class MainRepository @Inject constructor(
    private val apiHelper: ApiHelper,
    private val preferencesHelper: PreferencesHelper
) {

    fun getUserState(): UserState? {
        return preferencesHelper.getUserState()
    }

    fun setUserState(userState: UserState) {
        preferencesHelper.setUserState(userState)
    }

    fun getUserEmail(): String? {
        return preferencesHelper.getUserEmail()
    }

    fun setUserEmail(email: String) {
        preferencesHelper.setUserEmail(email)
    }

    fun getName(): String? {
        return preferencesHelper.getName()
    }

    fun setName(name: String) {
        preferencesHelper.setName(name)
    }

    fun getFirstName(): String? {
        return preferencesHelper.getFirstName()
    }

    fun setFirstName(firstName: String) {
        preferencesHelper.setFirstName(firstName)
    }

    fun getLastName(): String? {
        return preferencesHelper.getLastName()
    }

    fun setLastName(lastName: String) {
        preferencesHelper.setLastName(lastName)
    }

    fun getMobile(): String? {
        return preferencesHelper.getMobile()
    }

    fun setMobile(mobile: String) {
        preferencesHelper.setMobile(mobile)
    }

    fun getUserLoggedIn(): Boolean {
        return preferencesHelper.getUserLoggedIn()
    }

    fun setUserLoggedIn() {
        preferencesHelper.setUserLoggedIn()
    }

    fun getProfilePicture(): String? {
        return preferencesHelper.getProfilePicture()
    }

    fun setProfilePicture(profilePictureUrl: String) {
        preferencesHelper.setProfilePicture(profilePictureUrl)
    }

    fun getAccessToken(): String? {
        return preferencesHelper.getAccessToken()
    }

    fun setAccessToken(accessToken: String) {
        preferencesHelper.setAccessToken(accessToken)
    }

    fun getUserId(): Int {
        return preferencesHelper.getUserId()
    }

    fun setUserId(userId: Int) {
        preferencesHelper.setUserId(userId)
    }

    suspend fun signup(body: Map<String, String>) = apiHelper.signup(body)

    suspend fun tags(token: String?, userId: Int) = apiHelper.tags(token, userId)

}