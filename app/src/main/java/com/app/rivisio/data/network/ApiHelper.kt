package com.app.rivisio.data.network

import com.app.rivisio.ui.add_topic.Topic
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiHelper {
    suspend fun signup(body: Map<String, String>): Response<JsonElement>
    suspend fun tags(token: String?, userId: Int): Response<JsonElement>
    suspend fun addTag(
        token: String?,
        userId: Int,
        body: Map<String, String>,
    ): Response<JsonElement>

    suspend fun uploadImages(
        userId: RequestBody,
        token: RequestBody,
        file: MultipartBody.Part,
        fileName: RequestBody
    ): Response<JsonElement>

    suspend fun addTopic(
        token: String?,
        userId: Int,
        topic: Topic
    ): Response<JsonElement>

    suspend fun getTopics(
        token: String?,
        userId: Int
    ): Response<JsonElement>
}