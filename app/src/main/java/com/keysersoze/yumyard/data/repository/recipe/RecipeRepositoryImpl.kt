package com.keysersoze.yumyard.data.repository.recipe

import com.google.firebase.firestore.FirebaseFirestore
import com.keysersoze.yumyard.data.local.RecipeCache
import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.model.RecipeFeedPage
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cache: RecipeCache
) : RecipeRepository {

    override suspend fun search(query: String): List<Recipe> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return emptyList()
        return firestore.collection("recipes")
            .orderBy("titleLower")
            .startAt(q)
            .endAt(q + "")
            .limit(30)
            .get()
            .awaitResult()
            .documents
            .mapNotNull { it.toRecipe() }
    }

    override suspend fun getRandomFeed(
        afterRandom: Double,
        limit: Int,
        isFirstPage: Boolean
    ): RecipeFeedPage {
        return try {
            val docs = firestore.collection("recipes")
                .orderBy("random")
                .startAfter(afterRandom)
                .limit(limit.toLong())
                .get()
                .awaitResult()
                .documents

            val recipes = docs.mapNotNull { it.toRecipe() }
            val nextCursor = if (docs.size >= limit) docs.lastOrNull()?.getDouble("random") else null

            if (isFirstPage && recipes.isNotEmpty()) cache.saveHomeRecipes(recipes)
            RecipeFeedPage(recipes = recipes, nextCursor = nextCursor)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (isFirstPage) {
                RecipeFeedPage(recipes = cache.getHomeRecipes(), nextCursor = null, fromCache = true)
            } else {
                RecipeFeedPage(recipes = emptyList(), nextCursor = null)
            }
        }
    }
}
