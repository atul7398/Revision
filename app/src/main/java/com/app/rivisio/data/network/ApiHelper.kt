package com.app.rivisio.data.network

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Query

interface ApiHelper {
    suspend fun signup(body: Map<String, String>): Response<JsonElement>
    suspend fun tags(token: String?, userId: Int): Response<JsonElement>

    suspend fun addTag(
        token: String?,
        userId: Int,
        body: Map<String, String>,
    ): Response<JsonElement>
}