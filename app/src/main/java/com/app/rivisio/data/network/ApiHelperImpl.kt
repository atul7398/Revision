package com.app.rivisio.data.network

import com.google.gson.JsonElement
import retrofit2.Response
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


}