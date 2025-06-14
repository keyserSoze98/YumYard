package com.keysersoze.yumyard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.keysersoze.yumyard.data.local.dao.FavoriteDao
import com.keysersoze.yumyard.data.local.entities.FavoriteEntity

@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = false)
abstract class YumYardDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}