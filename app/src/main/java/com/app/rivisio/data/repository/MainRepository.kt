package com.app.rivisio.data.repository

import com.app.rivisio.data.network.*
import com.app.rivisio.data.prefs.PreferencesHelper
import com.app.rivisio.data.prefs.UserState
import com.app.rivisio.ui.add_topic.Topic
import com.google.gson.JsonElement
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part
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

    suspend fun addTag(
        token: String?,
        userId: Int,
        body: Map<String, String>,
    ) = apiHelper.addTag(token, userId, body)

    suspend fun uploadImages(
        userId: RequestBody,
        token: RequestBody,
        file: MultipartBody.Part,
        fileName: RequestBody
    ): Response<JsonElement> = apiHelper.uploadImages(userId, token, file, fileName)

    suspend fun addTopic(
        token: String?,
        userId: Int,
        topic: Topic
    ) = apiHelper.addTopic(token, userId, topic)

    suspend fun getTopics(
        token: String?,
        userId: Int
    ) = apiHelper.getTopics(token, userId)

    suspend fun getTopicsData(
        token: String?,
        userId: Int
    ) = apiHelper.getTopicsData(token, userId)
}