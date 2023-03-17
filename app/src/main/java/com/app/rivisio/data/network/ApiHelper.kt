package com.app.rivisio.data.network

import com.google.gson.JsonElement
import retrofit2.Response

interface ApiHelper {

    suspend fun getUsers(): Response<JsonElement>
}