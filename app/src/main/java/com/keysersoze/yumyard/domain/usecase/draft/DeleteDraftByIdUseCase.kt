package com.keysersoze.yumyard.domain.usecase.draft

import com.keysersoze.yumyard.data.repository.draft.DraftRecipeRepository
import javax.inject.Inject

class DeleteDraftByIdUseCase @Inject constructor(
    private val repository: DraftRecipeRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteDraftById(id)
    }
}