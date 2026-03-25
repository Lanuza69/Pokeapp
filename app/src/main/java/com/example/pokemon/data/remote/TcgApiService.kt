package com.example.pokemon.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TcgApiService {
    @GET("cards")
    suspend fun getCards(
        @Query("q") query: String,
        @Query("pageSize") pageSize: Int = 1
    ): TcgResponse
}
