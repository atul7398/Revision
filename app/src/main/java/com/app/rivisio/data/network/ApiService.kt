package com.app.rivisio.data.network

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    suspend fun getUsers(): Response<JsonElement>

}