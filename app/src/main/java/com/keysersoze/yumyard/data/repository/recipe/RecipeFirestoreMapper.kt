package com.keysersoze.yumyard.data.repository.recipe

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.keysersoze.yumyard.domain.model.Recipe
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun <T> Task<T>.awaitResult(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
    }

@Suppress("UNCHECKED_CAST")
internal fun DocumentSnapshot.toRecipe(): Recipe? {
    if (!exists()) return null

    val legacyIngredients = (1..20).mapNotNull { i ->
        val ingredient = getString("strIngredient$i")?.takeIf { it.isNotBlank() }
            ?: return@mapNotNull null
        val measure = getString("strMeasure$i")?.takeIf { it.isNotBlank() }
        if (measure != null) "$measure $ingredient" else ingredient
    }
    val legacySteps = getString("strInstructions")
        ?.split("\n")
        ?.map { it.trim().replace(Regex("^\\d+\\.\\s*"), "") }
        ?.filter { it.isNotBlank() }
        ?: emptyList()

    return Recipe(
        id = getString("id") ?: getString("idMeal") ?: id,
        title = getString("title") ?: getString("strMeal") ?: "",
        description = getString("description") ?: getString("strDescription") ?: "",
        cuisine = getString("cuisine") ?: getString("strArea") ?: "",
        imageUrl = getString("imageUrl") ?: getString("strMealThumb") ?: "",
        ingredients = (get("ingredients") as? List<String>) ?: legacyIngredients,
        steps = (get("steps") as? List<String>) ?: legacySteps,
        readyInMinutes = (getLong("readyInMinutes") ?: 0L).toInt(),
        servings = (getLong("servings") ?: 0L).toInt(),
        rating = getDouble("rating") ?: 0.0,
        difficulty = getString("difficulty") ?: "",
        category = getString("category") ?: "",
        author = getString("author") ?: ""
    )
}
