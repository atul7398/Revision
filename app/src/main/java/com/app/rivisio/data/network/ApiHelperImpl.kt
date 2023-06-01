package com.app.rivisio.data.network

import com.app.rivisio.ui.add_topic.Topic
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun signup(body: Map<String, String>) = apiService.signup(body)

    override suspend fun tags(token: String?, userId: Int) = apiService.tags(token, userId)

    override suspend fun addTag(
        token: String?,
        userId: Int,
        body: Map<String, String>
    ) = apiService.addTag(token, userId, body)

    override suspend fun uploadImages(
        userId: RequestBody,
        token: RequestBody,
        file: MultipartBody.Part,
        fileName: RequestBody
    ) = apiService.uploadImages(userId, token, file, fileName)

    override suspend fun addTopic(
        token: String?,
        userId: Int,
        topic: Topic
    ) = apiService.addTopic(token, userId, topic)

    override suspend fun getTopics(
        token: String?,
        userId: Int
    ) = apiService.getTopics(token, userId)

    override suspend fun getTopicsData(token: String?, userId: Int) =
        apiService.getTopicsData(token, userId)

    override suspend fun getAllTopics(
        token: String?,
        userId: Int
    ) = apiService.getAllTopics(token, userId)

    override suspend fun getTopicDetails(
        topicId: Int,
        token: String?,
        userId: Int
    ) = apiService.getTopicDetails(topicId, token, userId)

    override suspend fun editTopicDetails(
        topicId: Int,
        token: String?,
        userId: Int,
        topicFromServer: TopicFromServer
    ) = apiService.editTopicDetails(topicId, token, userId, topicFromServer)

    override suspend fun reviseTopic(
        topicId: Int,
        body: Map<String, String>
    ) = apiService.reviseTopic(topicId, body)

    override suspend fun getUser(token: String?, userId: Int) = apiService.getUser(token, userId)
    override suspend fun getUserStats(token: String?, userId: Int) =
        apiService.getUserStats(token, userId)

    override suspend fun limitcheck(
        token: String?,
        userId: Int
    ) = apiService.limitcheck(token, userId)
}