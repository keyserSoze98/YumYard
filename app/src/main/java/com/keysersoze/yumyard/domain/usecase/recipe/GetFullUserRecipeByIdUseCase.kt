package com.keysersoze.yumyard.domain.usecase.recipe

import com.google.firebase.firestore.FirebaseFirestore
import com.keysersoze.yumyard.data.repository.recipe.toRecipe
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
            .document(id)
            .get()
            .addOnSuccessListener { doc ->
                val recipe = doc.toRecipe()
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
