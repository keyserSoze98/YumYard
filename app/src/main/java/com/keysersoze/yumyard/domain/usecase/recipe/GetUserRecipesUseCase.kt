package com.keysersoze.yumyard.domain.usecase.recipe

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.keysersoze.yumyard.domain.model.Recipe
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GetUserRecipesUseCase @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getAllUserRecipes(): List<Recipe> = suspendCoroutine { cont ->
        firestore.collection("user_recipes")
            .get()
            .addOnSuccessListener { snapshot ->
                val recipes = snapshot.documents.mapNotNull { mapToRecipe(it) }
                cont.resume(recipes)
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }

    suspend fun searchRecipesByTitle(query: String): List<Recipe> = suspendCoroutine { cont ->
        firestore.collection("user_recipes")
            .whereGreaterThanOrEqualTo("strMealLower", query)
            .whereLessThanOrEqualTo("strMealLower", query + "\uf8ff")
            .get()
            .addOnSuccessListener { snapshot ->
                val recipes = snapshot.documents.mapNotNull { mapToRecipe(it) }
                cont.resume(recipes)
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }

    private fun mapToRecipe(doc: DocumentSnapshot): Recipe? {
        return Recipe(
            id = doc.getString("idMeal") ?: "",
            title = doc.getString("strMeal") ?: "",
            description = doc.getString("strDescription") ?: "",
            cuisine = doc.getString("strArea") ?: "",
            imageUrl = doc.getString("strMealThumb") ?: "",
            ingredients = listOfNotNull(
                doc.getString("strIngredient1"),
                doc.getString("strIngredient2"),
                doc.getString("strIngredient3"),
                doc.getString("strIngredient4"),
                doc.getString("strIngredient5"),
                doc.getString("strIngredient6"),
                doc.getString("strIngredient7")
            ).filter { it.isNotBlank() },
            steps = doc.getString("strInstructions")
                ?.split("\n")
                ?.map { it.trim().replace(Regex("^\\d+\\.\\s*"), "") }
                ?.filter { it.isNotBlank() }
                ?: emptyList()
        )
    }

}