package com.keysersoze.yumyard.presentation.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.keysersoze.yumyard.data.local.entities.UserRecipeDraftEntity
import com.keysersoze.yumyard.data.repository.draft.DraftRecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val draftRepository: DraftRecipeRepository
) : ViewModel() {

    private val _draft = MutableStateFlow<UserRecipeDraftEntity?>(null)
    val draft: StateFlow<UserRecipeDraftEntity?> = _draft

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    fun loadDraft(draftId: String) {
        viewModelScope.launch {
            _draft.value = draftRepository.getDraftById(draftId) ?: UserRecipeDraftEntity(
                id = draftId,
                title = "",
                cuisine = "",
                description = "",
                imageUrl = "",
                ingredients = listOf(Pair("", "")),
                steps = "",
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    fun updateTitle(newTitle: String) {
        updateDraft { it.copy(title = newTitle, lastUpdated = System.currentTimeMillis()) }
    }

    fun updateCuisine(newCuisine: String) {
        updateDraft { it.copy(cuisine = newCuisine, lastUpdated = System.currentTimeMillis()) }
    }

    fun updateDescription(newDesc: String) {
        updateDraft { it.copy(description = newDesc, lastUpdated = System.currentTimeMillis()) }
    }

    fun updateImageUrl(url: String) {
        updateDraft { it.copy(imageUrl = url, lastUpdated = System.currentTimeMillis()) }
    }

    fun uploadImageAndSetUrl(uri: Uri, context: Context) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val storageRef = FirebaseStorage.getInstance().reference.child("recipe_images/$fileName")

        storageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Upload failed")
                }
                storageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                updateImageUrl(downloadUrl.toString())
                Toast.makeText(context, "Image uploaded!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    fun updateSteps(steps: String) {
        updateDraft { it.copy(steps = steps, lastUpdated = System.currentTimeMillis()) }
    }

    fun updateIngredient(index: Int, name: String) {
        updateDraft {
            val updatedIngredients = it.ingredients.toMutableList()
            updatedIngredients[index] = updatedIngredients[index].copy(first = name)
            it.copy(ingredients = updatedIngredients, lastUpdated = System.currentTimeMillis())
        }
    }

    fun removeIngredient(index: Int) {
        updateDraft {
            val updatedIngredients = it.ingredients.toMutableList().apply {
                removeAt(index)
            }
            it.copy(ingredients = updatedIngredients, lastUpdated = System.currentTimeMillis())
        }
    }

    fun updateMeasure(index: Int, measure: String) {
        updateDraft {
            val updatedIngredients = it.ingredients.toMutableList()
            updatedIngredients[index] = updatedIngredients[index].copy(second = measure)
            it.copy(ingredients = updatedIngredients, lastUpdated = System.currentTimeMillis())
        }
    }

    fun addEmptyIngredient() {
        updateDraft {
            it.copy(
                ingredients = it.ingredients + Pair("", ""),
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    fun saveDraft() {
        viewModelScope.launch {
            _draft.value?.let {
                draftRepository.upsertDraft(it)
            }
        }
    }

    fun deleteDraft(id: String) {
        viewModelScope.launch {
            draftRepository.deleteDraftById(id)
        }
    }

    fun uploadRecipe(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onError("User not logged in")
            Log.d("UPLOAD_RECIPE", "User not logged in")
            return
        }

        val draftData = _draft.value
        if (draftData == null) {
            Log.d("UPLOAD_RECIPE", "Draft data is null")
            onError("Draft data is null")
            return
        }
        if (draftData.title.isBlank() || draftData.steps.isBlank()) {
            Log.d("UPLOAD_RECIPE", "Incomplete data: title or steps blank")
            onError("Incomplete recipe data")
            return
        }

        _isUploading.value = true

        val dataMap = hashMapOf<String, Any>(
            "idMeal" to draftData.id,
            "strMeal" to draftData.title,
            "strMealLower" to draftData.title.lowercase(),
            "strArea" to draftData.cuisine,
            "strDescription" to draftData.description,
            "strInstructions" to draftData.steps,
            "strMealThumb" to draftData.imageUrl,
            "author" to (user.displayName ?: "Anonymous"),
            "authorId" to user.uid,
            "createdAt" to System.currentTimeMillis()
        )

        // Add ingredients and measures
        draftData.ingredients.forEachIndexed { index, (ingredient, measure) ->
            dataMap["strIngredient${index + 1}"] = ingredient
            dataMap["strMeasure${index + 1}"] = measure
        }

        Log.d("UPLOAD_RECIPE", "Uploading data: $dataMap")

        FirebaseFirestore.getInstance()
            .collection("user_recipes")
            .document(draftData.id)
            .set(dataMap)
            .addOnSuccessListener {
                Log.d("UPLOAD_RECIPE", "Upload success!")
                deleteDraft(draftData.id)
                _isUploading.value = false
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("UPLOAD_RECIPE", "Upload failed: ${e.message}", e)
                _isUploading.value = false
                onError(e.message ?: "Upload failed")
            }
    }

    private fun updateDraft(transform: (UserRecipeDraftEntity) -> UserRecipeDraftEntity) {
        _draft.value = _draft.value?.let { transform(it) }
    }
}