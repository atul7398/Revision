package com.app.rivisio.ui.add_topic

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
class AddTopicViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

    private val _tags = MutableLiveData<NetworkResult<JsonElement>>()
    val tags: LiveData<NetworkResult<JsonElement>>
        get() = _tags

    fun getTopics() {
        viewModelScope.launch {

            _tags.value = NetworkResult.Loading

            val networkResponse = handleApi {
                mainRepository.tags(
                mainRepository.getAccessToken(),
                mainRepository.getUserId()
                )
            }

        }
    }
}