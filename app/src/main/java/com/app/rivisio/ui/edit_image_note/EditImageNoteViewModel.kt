package com.app.rivisio.ui.edit_image_note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.MainRepository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
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
class EditImageNoteViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

    private val _topicData = MutableLiveData<NetworkResult<JsonElement>>()
    val topicData: LiveData<NetworkResult<JsonElement>>
        get() = _topicData

    private val _update = MutableLiveData<NetworkResult<JsonElement>>()
    val update: LiveData<NetworkResult<JsonElement>>
        get() = _update

    private val _imageUploaded = MutableLiveData<NetworkResult<JsonElement>>()
    val imageUploaded: LiveData<NetworkResult<JsonElement>>
        get() = _imageUploaded

    fun getTopicDetails(topicId: Int) {

        viewModelScope.launch {

            _topicData.value = NetworkResult.Loading

            val response = handleApi {
                mainRepository.getTopicDetails(
                    topicId,
                    mainRepository.getAccessToken(),
                    mainRepository.getUserId()
                )
            }

            _topicData.value = response

        }
    }

    fun updateImageNote(topicId: Int, topicFromServer: TopicFromServer) {

        viewModelScope.launch {

            _update.value = NetworkResult.Loading

            val response = handleApi {
                mainRepository.editTopicDetails(
                    topicId,
                    mainRepository.getAccessToken(),
                    mainRepository.getUserId(),
                    topicFromServer
                )
            }

            _update.value = response

        }

    }

    fun uploadImage(image: Image) {
        viewModelScope.launch {
            _imageUploaded.value = NetworkResult.Loading

            val file = File(image.path)
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val fileName = getFileNameWithoutExtension(file.name)

            val response = handleApi {
                mainRepository.uploadImages(
                    mainRepository.getUserId().toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    mainRepository.getAccessToken()!!
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    body,
                    fileName.toRequestBody("text/plain".toMediaTypeOrNull())
                )
            }

            _imageUploaded.value = response
        }
    }

    private fun getFileNameWithoutExtension(fileName: String): String {
        return if (fileName.indexOf(".") > 0) {
            fileName.substring(0, fileName.lastIndexOf("."))
        } else {
            fileName
        }
    }
}