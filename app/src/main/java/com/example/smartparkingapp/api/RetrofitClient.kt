package com.example.smartparkingapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Afeka IP: 172.20.20.96
    // Tomer's iphon IP: 172.20.10.6
    // Dira IP: 192.168.1.125
    // KfarYona: 192.168.68.107
    // ficus: 172.20.29.215
    // kirya: 172.20.26.173
    //192.168.56.1
    private const val BASE_URL = "http://192.168.68.107:8081/" // This points to localhost on your computer when running in the Android emulator

    // Create OkHttpClient with logging
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Create Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Create API service
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}