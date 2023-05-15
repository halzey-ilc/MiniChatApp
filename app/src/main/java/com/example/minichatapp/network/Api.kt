package com.example.minichatapp.network

import com.example.minichatapp.models.MessageBody
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface Api {

    @POST("send")
    suspend fun sendMessage(
        @HeaderMap header:HashMap<String,String>,
        @Body messageBody: MessageBody
    ): Response<JsonObject>

}