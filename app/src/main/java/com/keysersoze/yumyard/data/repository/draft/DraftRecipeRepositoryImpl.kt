package com.keysersoze.yumyard.data.repository.draft

import com.keysersoze.yumyard.data.local.dao.UserRecipeDraftDao
import com.keysersoze.yumyard.data.local.entities.UserRecipeDraftEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DraftRecipeRepositoryImpl @Inject constructor(
    private val dao: UserRecipeDraftDao
) : DraftRecipeRepository {

    override fun getAllDrafts(): Flow<List<UserRecipeDraftEntity>> = dao.getAllDrafts()

    override suspend fun saveDraft(draft: UserRecipeDraftEntity) = dao.upsertDraft(draft)

    override suspend fun deleteDraft(draft: UserRecipeDraftEntity) = dao.deleteDraft(draft)

    override suspend fun getDraftById(id: String): UserRecipeDraftEntity? = dao.getDraftById(id)

    override suspend fun upsertDraft(draft: UserRecipeDraftEntity) = dao.upsertDraft(draft)

    override suspend fun deleteDraftById(id: String) = dao.deleteDraftById(id)
}