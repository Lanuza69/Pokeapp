package com.example.pokemon.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val POKE_BASE_URL = "https://pokeapi.co/api/v2/"
    private const val TCG_BASE_URL = "https://api.pokemontcg.io/v2/"

    val pokeApi: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(POKE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }

    val tcgApi: TcgApiService by lazy {
        Retrofit.Builder()
            .baseUrl(TCG_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TcgApiService::class.java)
    }
}
