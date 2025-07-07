package com.keysersoze.yumyard.domain.usecase.recipe

import com.google.firebase.firestore.FirebaseFirestore
import com.keysersoze.yumyard.domain.model.Recipe
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GetFullUserRecipeByIdUseCase @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun execute(id: String): Recipe = suspendCoroutine { cont ->
        firestore.collection("user_recipes")
            .whereEqualTo("idMeal", id)
            .get()
            .addOnSuccessListener { snapshot ->
                val recipe = snapshot.documents.firstOrNull()?.let { doc ->
                    Recipe(
                        id = doc.getString("idMeal") ?: "",
                        title = doc.getString("strMeal") ?: "",
                        description = doc.getString("strDescription") ?: "",
                        cuisine = doc.getString("strArea") ?: "",
                        imageUrl = doc.getString("strMealThumb") ?: "",
                        ingredients = (1..7).mapNotNull { i ->
                            doc.getString("strIngredient$i")?.takeIf { it.isNotBlank() }
                        },
                        steps = doc.getString("strInstructions")
                            ?.split("\n")
                            ?.map { it.trim().replace(Regex("^\\d+\\.\\s*"), "") }
                            ?.filter { it.isNotBlank() }
                            ?: emptyList()
                    )
                }

                if (recipe != null) {
                    cont.resume(recipe)
                } else {
                    cont.resumeWithException(Exception("Recipe not found"))
                }
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }
}