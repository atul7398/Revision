package com.app.rivisio.ui.home.fragments.home_fragment

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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _topics = MutableLiveData<NetworkResult<JsonElement>>()
    val topics: LiveData<NetworkResult<JsonElement>>
        get() = _topics

    fun getUserDetails() {
        viewModelScope.launch {
            _userName.value = mainRepository.getName()
        }
    }

    fun getTopicsData() {
        viewModelScope.launch {
            _topics.value = NetworkResult.Loading

            val response = handleApi {
                mainRepository.getTopicsData(
                    mainRepository.getAccessToken()!!,
                    mainRepository.getUserId()
                )
            }

            _topics.value = response
        }
    }

}