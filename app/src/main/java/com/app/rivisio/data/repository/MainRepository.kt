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

    fun setUserEmail(email: String) {
        preferencesHelper.setUserEmail(email)
    }

    fun setName(name: String) {
        preferencesHelper.setName(name)
    }

    suspend fun getUsers() = apiHelper.getUsers()

}