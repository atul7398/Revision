package com.app.rivisio.ui.add_notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.MainRepository
import com.app.rivisio.ui.add_topic.Topic
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import com.esafirm.imagepicker.model.Image
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddNotesViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

    private val _imageUploaded = MutableLiveData<NetworkResult<JsonElement>>()
    val imageUploaded: LiveData<NetworkResult<JsonElement>>
        get() = _imageUploaded

    private val _topicAdded = MutableLiveData<NetworkResult<JsonElement>>()
    val topicAdded: LiveData<NetworkResult<JsonElement>>
        get() = _topicAdded

    fun uploadImage(image: Image) {
        viewModelScope.launch {
            _imageUploaded.value = NetworkResult.Loading

            val file = File(image.path)
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = handleApi {
                mainRepository.uploadImages(
                    mainRepository.getUserId().toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    mainRepository.getAccessToken()!!
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    body,
                    "image_note".toRequestBody("text/plain".toMediaTypeOrNull())
                )
            }

            _imageUploaded.value = response
        }
    }

    fun addTopic(topic: Topic) {
        viewModelScope.launch {
            _topicAdded.value = NetworkResult.Loading

            val response = handleApi {
                mainRepository.addTopic(
                    mainRepository.getAccessToken()!!,
                    mainRepository.getUserId(),
                    topic
                )
            }

            _topicAdded.value = response

        }
    }
}