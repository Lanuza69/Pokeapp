package com.example.pokemon

import android.app.Application
import com.example.pokemon.data.remote.RetrofitClient

class PokemonApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}
