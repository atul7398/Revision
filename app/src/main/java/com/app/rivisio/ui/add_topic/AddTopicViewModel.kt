package com.app.rivisio.ui.add_topic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.network.DOB
import com.app.rivisio.data.network.EMAIL
import com.app.rivisio.data.network.HEX_CODE
import com.app.rivisio.data.network.NAME
import com.app.rivisio.data.prefs.UserState
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
class AddTopicViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

    private val _tags = MutableLiveData<NetworkResult<JsonElement>>()
    val tags: LiveData<NetworkResult<JsonElement>>
        get() = _tags

    private val _addTags = MutableLiveData<NetworkResult<JsonElement>>()
    val addTags: LiveData<NetworkResult<JsonElement>>
        get() = _addTags

    fun getTopics() {
        viewModelScope.launch {
            _tags.value = NetworkResult.Loading
            val networkResponse = handleApi {
                mainRepository.tags(
                    mainRepository.getAccessToken(),
                    mainRepository.getUserId()
                )
            }
            _tags.value = networkResponse
        }
    }

    fun addTag(tagName: String, tagColor: String) {
        viewModelScope.launch {
            _addTags.value = NetworkResult.Loading

            val requestBody = mutableMapOf<String, String>()
            requestBody[HEX_CODE] = tagColor.trim()
            requestBody[NAME] = tagName.trim()

            val networkResponse = handleApi {
                mainRepository.addTag(
                    mainRepository.getAccessToken(),
                    mainRepository.getUserId(),
                    requestBody
                )
            }

            _addTags.value = networkResponse
        }
    }
}