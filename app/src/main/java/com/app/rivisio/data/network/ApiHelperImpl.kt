package com.app.rivisio.data.network

import com.google.gson.JsonElement
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun getUsers(): Response<JsonElement> = apiService.getUsers()

}