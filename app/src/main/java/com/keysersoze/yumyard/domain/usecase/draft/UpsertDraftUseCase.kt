package com.keysersoze.yumyard.domain.usecase.draft

import com.keysersoze.yumyard.data.local.entities.UserRecipeDraftEntity
import com.keysersoze.yumyard.data.repository.draft.DraftRecipeRepository
import javax.inject.Inject

class UpsertDraftUseCase @Inject constructor(
    private val repository: DraftRecipeRepository
) {
    suspend operator fun invoke(draft: UserRecipeDraftEntity) {
        repository.upsertDraft(draft)
    }
}