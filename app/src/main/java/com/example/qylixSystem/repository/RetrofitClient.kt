package com.example.qylixSystem.repository

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {

    private lateinit var service: RepositoryService

    fun getRepositoryService(): RepositoryService {
        if (!::service.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.nbrb.by")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
            service = retrofit.create(RepositoryService::class.java)
        }
        return service
    }

    }
