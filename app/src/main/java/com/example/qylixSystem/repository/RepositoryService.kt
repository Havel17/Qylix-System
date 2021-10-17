package com.example.qylixSystem.repository

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RepositoryService {
    @GET("/Services/XmlExRates.aspx")
     fun getCurrencies(@Query("ondate") date: String): Call<String>
}