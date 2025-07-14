package com.example.readstack.api

import retrofit2.http.GET
import retrofit2.http.Query

interface BookApi {
 @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): ApiResponse
}