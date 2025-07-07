package com.keysersoze.yumyard.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.keysersoze.yumyard.data.local.YumYardDatabase
import com.keysersoze.yumyard.data.local.dao.FavoriteDao
import com.keysersoze.yumyard.data.local.dao.UserRecipeDraftDao
import com.keysersoze.yumyard.data.remote.response.RecipeApiService
import com.keysersoze.yumyard.data.repository.draft.DraftRecipeRepository
import com.keysersoze.yumyard.data.repository.draft.DraftRecipeRepositoryImpl
import com.keysersoze.yumyard.data.repository.favorite.FavoriteRepository
import com.keysersoze.yumyard.data.repository.favorite.FavoriteRepositoryImpl
import com.keysersoze.yumyard.domain.usecase.draft.DeleteDraftByIdUseCase
import com.keysersoze.yumyard.domain.usecase.draft.DeleteDraftUseCase
import com.keysersoze.yumyard.domain.usecase.draft.DraftUseCases
import com.keysersoze.yumyard.domain.usecase.draft.GetAllDraftsUseCase
import com.keysersoze.yumyard.domain.usecase.draft.GetDraftByIdUseCase
import com.keysersoze.yumyard.domain.usecase.draft.SaveDraftUseCase
import com.keysersoze.yumyard.domain.usecase.draft.UpsertDraftUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): YumYardDatabase {
        return Room.databaseBuilder(
            context,
            YumYardDatabase::class.java,
            "yumyard_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(dao: FavoriteDao, api: RecipeApiService): FavoriteRepository {
        return FavoriteRepositoryImpl(dao, api)
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: YumYardDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideUserRecipeDraftDao(database: YumYardDatabase): UserRecipeDraftDao {
        return database.draftDao()
    }

    @Provides
    @Singleton
    fun provideDraftRepository(dao: UserRecipeDraftDao): DraftRecipeRepository {
        return DraftRecipeRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideDraftUseCases(repository: DraftRecipeRepository): DraftUseCases {
        return DraftUseCases(
            getAllDraftsUseCase = GetAllDraftsUseCase(repository),
            saveDraftUseCase = SaveDraftUseCase(repository),
            deleteDraftUseCase = DeleteDraftUseCase(repository),
            getDraftByIdUseCase = GetDraftByIdUseCase(repository),
            deleteDraftByIdUseCase = DeleteDraftByIdUseCase(repository),
            upsertDraftUseCase = UpsertDraftUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}