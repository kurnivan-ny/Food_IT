package com.kurnivan_ny.foodit.data.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://34.126.156.251/" // URL VM
private const val TIME_OUT = 120L //s

class RetrofitInstance {
    // object retrofit 1 -> lebih aman
    companion object{
        private val retrofit by lazy {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            // json ke objek data (data class) -> key value

            val logging = HttpLoggingInterceptor()
            val level = HttpLoggingInterceptor.Level.BODY
            logging.setLevel(level)

            val client = OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }

        val API_OBJECT: ApiService by lazy {
            retrofit.create(ApiService::class.java)
        }
    }
}