package com.app.rivisio.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.MainRepository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

    private val _users = MutableLiveData<NetworkResult<JsonElement>>()
    val users: LiveData<NetworkResult<JsonElement>>
        get() = _users

    fun fetchUsers() {
        viewModelScope.launch {
            _users.value = NetworkResult.Loading

            val response = handleApi { mainRepository.getUsers() }
            _users.value = response

        }

    }
}
