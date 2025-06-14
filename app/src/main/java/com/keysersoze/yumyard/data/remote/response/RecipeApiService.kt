package com.keysersoze.yumyard.data.remote.response

import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {
    @GET("search.php")
    suspend fun searchRecipes(@Query("s") query: String): MealResponse

    @GET("random.php")
    suspend fun getRandomRecipe(): MealResponse

    @GET("lookup.php")
    suspend fun getRecipeById(@Query("i") id: String): MealResponse
}