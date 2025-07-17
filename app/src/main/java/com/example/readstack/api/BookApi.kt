package com.example.readstack.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 30
    ): ApiResponse

    @GET("{workKey}.json")
    suspend fun getBookDetails(@Path("workKey") workKey: String): BookDetailResponse

    @GET("subjects/{subject}.json")
    suspend fun getBooksBySubject(
        @Path("subject") subject: String,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0
    ): SubjectResponse

}