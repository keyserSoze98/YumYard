package com.keysersoze.yumyard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keysersoze.yumyard.data.local.entities.UserRecipeDraftEntity
import com.keysersoze.yumyard.domain.usecase.draft.DraftUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DraftViewModel @Inject constructor(
    private val draftUseCases: DraftUseCases
): ViewModel() {

    val drafts = draftUseCases.getAllDraftsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveDraft(draft: UserRecipeDraftEntity) {
        viewModelScope.launch {
            this@DraftViewModel.draftUseCases.saveDraftUseCase(draft)
        }
    }

    fun deleteDraft(draft: UserRecipeDraftEntity) {
        viewModelScope.launch {
            this@DraftViewModel.draftUseCases.deleteDraftUseCase(draft)
        }
    }
}