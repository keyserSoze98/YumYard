package com.keysersoze.yumyard.data.repository.favorite

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.keysersoze.yumyard.data.repository.recipe.awaitResult
import com.keysersoze.yumyard.data.repository.recipe.toRecipe
import com.keysersoze.yumyard.domain.model.Favorite
import com.keysersoze.yumyard.domain.model.Recipe
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FavoriteRepository {

    private fun favoritesRef(): CollectionReference? {
        val uid = auth.currentUser?.uid ?: return null
        return firestore.collection("users").document(uid).collection("favorites")
    }

    override fun getAllFavorites(): Flow<List<Favorite>> = callbackFlow {
        val ref = favoritesRef()
        if (ref == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val registration = ref.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            trySend(snapshot?.documents?.mapNotNull { it.toFavorite() } ?: emptyList())
        }
        awaitClose { registration.remove() }
    }

    override suspend fun addToFavorites(favorite: Favorite) {
        val ref = favoritesRef() ?: return
        ref.document(favorite.id).set(
            mapOf(
                "id" to favorite.id,
                "title" to favorite.title,
                "imageUrl" to favorite.imageUrl,
                "description" to favorite.description,
                "cuisine" to favorite.cuisine
            )
        )
    }

    override suspend fun deleteFromFavorites(favorite: Favorite) {
        val ref = favoritesRef() ?: return
        ref.document(favorite.id).delete()
    }

    override suspend fun isFavorite(id: String): Boolean {
        val ref = favoritesRef() ?: return false
        return ref.document(id).get().awaitResult().exists()
    }

    override suspend fun fetchFullRecipeById(id: String): Recipe {
        return firestore.collection("recipes").document(id).get().awaitResult().toRecipe()
            ?: throw Exception("Recipe not found")
    }

    override suspend fun clearAllFavorites() {
        val ref = favoritesRef() ?: return
        val snapshot = ref.get().awaitResult()
        for (doc in snapshot.documents) {
            doc.reference.delete()
        }
    }
}

private fun DocumentSnapshot.toFavorite(): Favorite? {
    return Favorite(
        id = getString("id") ?: id,
        title = getString("title") ?: "",
        imageUrl = getString("imageUrl") ?: "",
        description = getString("description") ?: "",
        cuisine = getString("cuisine") ?: ""
    )
}
