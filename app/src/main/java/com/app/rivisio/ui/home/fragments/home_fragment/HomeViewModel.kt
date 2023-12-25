package com.app.rivisio.ui.home.fragments.home_fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.add_notes.TextNote
import com.app.rivisio.ui.add_topic.TOPIC_NAME
import com.app.rivisio.ui.add_topic.Tag
import com.app.rivisio.ui.add_topic.Topic
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.CommonUtils
import com.app.rivisio.utils.NetworkHelper
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.stringifyJsonNote
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _topics = MutableLiveData<NetworkResult<JsonElement>>()
    val topics: LiveData<NetworkResult<JsonElement>>
        get() = _topics

    private val _update = MutableLiveData<NetworkResult<JsonElement>>()
    val update: LiveData<NetworkResult<JsonElement>>
        get() = _update

    private val _userStats = MutableLiveData<NetworkResult<JsonElement>>()
    val userStats: LiveData<NetworkResult<JsonElement>>
        get() = _userStats

    private val _dailyVocab = MutableLiveData<NetworkResult<JsonElement>>()
    val dailyVocab: LiveData<NetworkResult<JsonElement>>
        get() = _dailyVocab

    private val _topicVocab = MutableLiveData<NetworkResult<JsonElement>>()
    val topicVocab: LiveData<NetworkResult<JsonElement>>
        get() = _topicVocab

    private val _saveVocab = MutableLiveData<NetworkResult<JsonElement>>()
    val saveVocab: LiveData<NetworkResult<JsonElement>>
        get() = _saveVocab

    fun getUserDetails() {
        viewModelScope.launch {
            _userName.value = repository.getName()
        }
    }

    fun getTopicsData() {
        viewModelScope.launch {
            _topics.value = NetworkResult.Loading
            val response = handleApi {
                repository.getTopicsData(
                    repository.getAccessToken()!!,
                    repository.getUserId()
                )
            }
            _topics.value = response
        }
    }

    fun reviseTopic(id: Int?, revsion: Map<String, String>) {

        viewModelScope.launch {

            _update.value = NetworkResult.Loading

            val response = handleApi {
                repository.reviseTopic(
                    id!!,
                    revsion
                )
            }
            _update.value = response

        }

    }
    fun getUserStats() {
        viewModelScope.launch {
            viewModelScope.launch {

                _userStats.value = NetworkResult.Loading
                Timber.d("ATUL",repository.getAccessToken())
                val response = handleApi {
                    repository.getUserStats(
                        repository.getAccessToken()!!,
                        repository.getUserId()
                    )
                }
                _userStats.value = response

            }
        }
    }


    fun getDailyVocab() {
        viewModelScope.launch {
            viewModelScope.launch {

                _dailyVocab.value = NetworkResult.Loading

                val response = handleApi {
                    repository.getDailyVocab(
                        repository.getAccessToken()!!,
                        repository.getUserId()
                    )
                }
                _dailyVocab.value = response
            }
        }
    }

    fun getTopicVocab() {
        viewModelScope.launch {
            viewModelScope.launch {

                _topicVocab.value = NetworkResult.Loading
                val response = handleApi {
                    repository.getUserStats(
                        repository.getAccessToken()!!,
                        repository.getUserId()
                    )
                }
                _topicVocab.value = response

            }
        }
    }

    fun saveWordAsTopic(word: String, meaning: String) {
        viewModelScope.launch {
            viewModelScope.launch {
                _saveVocab.value = NetworkResult.Loading
                val topic = createTopicData(word,meaning)
                val response = handleApi {
                    repository.addTopic(
                        repository.getAccessToken()!!,
                        repository.getUserId(),
                        topic
                    )
                }
                _saveVocab.value = response
            }
        }
    }

    private fun createTopicData(word: String, meaning: String): Topic {
        val textNote= TextNote(word,meaning).stringifyJsonNote()
        val studiedOnDate = CommonUtils.getStudiedOnDateString()
        val tag:ArrayList<Int> = arrayListOf(2)
        return Topic(
            ArrayList<String>(),
            "Word : $word",
            textNote,
            studiedOnDate,
            tag
        )
    }

}