package com.keysersoze.yumyard.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.keysersoze.yumyard.domain.usecase.favorite.ClearAllFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val clearAllFavoritesUseCase: ClearAllFavoritesUseCase
) : ViewModel() {
    private val currentUser: FirebaseUser? = Firebase.auth.currentUser

    fun signOut() {
        Firebase.auth.signOut()
    }

    fun deleteAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                clearAllFavoritesUseCase()
                currentUser?.delete()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onError(task.exception?.message ?: "Something went wrong")
                    }
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to delete account")
            }
        }
    }

    fun updateDisplayName(name: String, onComplete: (Boolean) -> Unit) {
        currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
            ?.addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    fun updatePhotoUrl(url: Uri, onComplete: (Boolean) -> Unit) {
        currentUser?.updateProfile(UserProfileChangeRequest.Builder().setPhotoUri(url).build())
            ?.addOnCompleteListener { onComplete(it.isSuccessful) }
    }
}