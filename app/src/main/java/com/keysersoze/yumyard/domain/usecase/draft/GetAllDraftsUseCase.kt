package com.keysersoze.yumyard.domain.usecase.draft

import com.keysersoze.yumyard.data.local.entities.UserRecipeDraftEntity
import com.keysersoze.yumyard.data.repository.draft.DraftRecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllDraftsUseCase @Inject constructor(
    private val repository: DraftRecipeRepository
) {
    operator fun invoke(): Flow<List<UserRecipeDraftEntity>> {
        return repository.getAllDrafts()
    }
}