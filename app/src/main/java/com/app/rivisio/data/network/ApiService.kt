package com.app.rivisio.data.network

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("/users/signup")
    suspend fun signup(
        @Body body: Map<String, String>
    ): Response<JsonElement>

}