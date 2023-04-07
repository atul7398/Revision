package com.app.rivisio.data.repository

import com.app.rivisio.data.network.ApiHelper
import com.app.rivisio.data.prefs.PreferencesHelper
import com.app.rivisio.data.prefs.UserState
import dagger.hilt.android.scopes.ViewModelScoped
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

    suspend fun signup(body: Map<String, String>) = apiHelper.signup(body)

}