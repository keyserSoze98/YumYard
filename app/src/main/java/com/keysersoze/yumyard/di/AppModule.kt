package com.keysersoze.yumyard.di

import android.content.Context
import androidx.room.Room
import com.keysersoze.yumyard.data.local.YumYardDatabase
import com.keysersoze.yumyard.data.local.dao.FavoriteDao
import com.keysersoze.yumyard.data.remote.response.RecipeApiService
import com.keysersoze.yumyard.data.repository.favorite.FavoriteRepository
import com.keysersoze.yumyard.data.repository.favorite.FavoriteRepositoryImpl
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(dao: FavoriteDao, api: RecipeApiService): FavoriteRepository {
        return FavoriteRepositoryImpl(dao, api)
    }

    @Provides
    fun provideFavoriteDao(database: YumYardDatabase): FavoriteDao {
        return database.favoriteDao()
    }
}