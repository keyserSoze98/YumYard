package com.keysersoze.yumyard.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.keysersoze.yumyard.data.local.entities.UserRecipeDraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRecipeDraftDao {
    @Query("SELECT * FROM user_recipe_drafts ORDER BY lastUpdated DESC")
    fun getAllDrafts(): Flow<List<UserRecipeDraftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDraft(draft: UserRecipeDraftEntity)

    @Delete
    suspend fun deleteDraft(draft: UserRecipeDraftEntity)
}