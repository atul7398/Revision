package com.app.rivisio.data.network

import com.app.rivisio.ui.add_topic.Topic
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Query
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
}