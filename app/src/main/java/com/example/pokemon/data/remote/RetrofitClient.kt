package com.example.pokemon.data.remote

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.util.Log
import com.google.gson.GsonBuilder

object RetrofitClient {

    private var okHttpClient: OkHttpClient? = null

    fun init(context: Context, enableCache: Boolean = true) {
        if (okHttpClient != null) return

        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(logging)

        if (enableCache) {
            try {
                val cacheSize = 10L * 1024 * 1024 // 10 MB
                val cache = Cache(context.cacheDir, cacheSize)
                builder.cache(cache)
                builder.addNetworkInterceptor { chain ->
                    val request = chain.request()
                    val response = chain.proceed(request)
                    val maxAge = 60 * 60 // 1 hour
                    response.newBuilder()
                        .header("Cache-Control", "public, max-age=$maxAge")
                        .build()
                }
            } catch (e: Exception) {
                Log.w("RetrofitClient", "Cache no disponible: ${e.localizedMessage}")
            }
        }

        okHttpClient = builder.build()
        Log.d("RetrofitClient", "OkHttpClient creado")
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        val client = okHttpClient
            ?: throw IllegalStateException("RetrofitClient not initialized. Call RetrofitClient.init(context) first.")
        val gson = GsonBuilder().create()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}
