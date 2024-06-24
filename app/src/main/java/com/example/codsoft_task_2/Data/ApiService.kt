package com.example.codsoft_task_2.Data

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("quotes/random")
    suspend fun getDate() : Response<Quote>
}