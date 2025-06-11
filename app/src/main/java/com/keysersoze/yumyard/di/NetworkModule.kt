package com.keysersoze.yumyard.di

import com.keysersoze.yumyard.data.remote.response.RecipeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecipeApiClient {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val api: RecipeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeApiService::class.java)
    }
}