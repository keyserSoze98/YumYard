package com.keysersoze.yumyard.domain.usecase.recipe

import com.google.firebase.firestore.FirebaseFirestore
import com.keysersoze.yumyard.data.repository.recipe.toRecipe
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
                val recipes = snapshot.documents.mapNotNull { it.toRecipe() }
                cont.resume(recipes)
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }

    suspend fun searchRecipesByTitle(query: String): List<Recipe> = suspendCoroutine { cont ->
        firestore.collection("user_recipes")
            .get()
            .addOnSuccessListener { snapshot ->
                val recipes = snapshot.documents
                    .mapNotNull { it.toRecipe() }
                    .filter { it.title.contains(query, ignoreCase = true) }
                cont.resume(recipes)
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }

    suspend fun getRecipesByAuthor(authorId: String): List<Recipe> = suspendCoroutine { cont ->
        firestore.collection("user_recipes")
            .whereEqualTo("authorId", authorId)
            .get()
            .addOnSuccessListener { snapshot ->
                val recipes = snapshot.documents.mapNotNull { it.toRecipe() }
                cont.resume(recipes)
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }

    suspend fun deleteRecipe(id: String): Unit = suspendCoroutine { cont ->
        firestore.collection("user_recipes")
            .document(id)
            .delete()
            .addOnSuccessListener { cont.resume(Unit) }
            .addOnFailureListener { e -> cont.resumeWithException(e) }
    }
}
