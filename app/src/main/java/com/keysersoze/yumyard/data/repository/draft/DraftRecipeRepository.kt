package com.keysersoze.yumyard.data.repository.draft

import com.keysersoze.yumyard.data.local.entities.UserRecipeDraftEntity
import kotlinx.coroutines.flow.Flow

interface DraftRecipeRepository {
    fun getAllDrafts(): Flow<List<UserRecipeDraftEntity>>
    suspend fun saveDraft(draft: UserRecipeDraftEntity)
    suspend fun deleteDraft(draft: UserRecipeDraftEntity)
    suspend fun getDraftById(id: String): UserRecipeDraftEntity?
    suspend fun upsertDraft(draft: UserRecipeDraftEntity)
    suspend fun deleteDraftById(id: String)
}
