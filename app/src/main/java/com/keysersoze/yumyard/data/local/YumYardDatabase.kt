package com.keysersoze.yumyard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.keysersoze.yumyard.data.local.converters.Converters
import com.keysersoze.yumyard.data.local.dao.FavoriteDao
import com.keysersoze.yumyard.data.local.dao.UserRecipeDraftDao
import com.keysersoze.yumyard.data.local.entities.FavoriteEntity
import com.keysersoze.yumyard.data.local.entities.UserRecipeDraftEntity

@Database(
    entities = [
        FavoriteEntity::class,
        UserRecipeDraftEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class YumYardDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun draftDao(): UserRecipeDraftDao
}