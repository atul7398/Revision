package com.app.rivisio.data.network

import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/users/signup")
    suspend fun signup(
        @Body body: Map<String, String>
    ): Response<JsonElement>

    @GET("/tag/all")
    suspend fun tags(
        @Query(TOKEN) token: String?,
        @Query(USER_ID) userId: Int,
    ): Response<JsonElement>

    @POST("/tag/add")
    suspend fun addTag(
        @Query(TOKEN) token: String?,
        @Query(USER_ID) userId: Int,
        @Body body: Map<String, String>,
    ): Response<JsonElement>

    @Multipart
    @POST("/users/uploadimage")
    suspend fun uploadImages(
        @Part(CUST_ID) userId: RequestBody,
        @Part(TOKEN) token: RequestBody,
        @Part file: MultipartBody.Part,
        @Part(FILENAME) fileName: RequestBody
    ): Response<JsonElement>
}