package com.app.rivisio.data.repository

import com.app.rivisio.data.network.ApiHelper
import com.app.rivisio.data.prefs.PreferencesHelper
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class MainRepository @Inject constructor(
    private val apiHelper: ApiHelper,
    private val preferencesHelper: PreferencesHelper
) {

    suspend fun getUsers() = apiHelper.getUsers()

}