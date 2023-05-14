package com.app.rivisio.ui.home.fragments.topics

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
class TopicViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

    private val _topics = MutableLiveData<NetworkResult<JsonElement>>()
    val topics: LiveData<NetworkResult<JsonElement>>
        get() = _topics

    fun getTopicsData() {
        viewModelScope.launch {
            _topics.value = NetworkResult.Loading

            val response = handleApi {
                mainRepository.getAllTopics(
                    mainRepository.getAccessToken()!!,
                    mainRepository.getUserId()
                )
            }

            _topics.value = response
        }
    }

}