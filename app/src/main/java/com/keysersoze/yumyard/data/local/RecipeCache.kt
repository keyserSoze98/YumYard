package com.keysersoze.yumyard.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.keysersoze.yumyard.domain.model.Recipe
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RecipeCache @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val homeKey = stringPreferencesKey("home_recipes")

    suspend fun saveHomeRecipes(recipes: List<Recipe>) {
        dataStore.edit { prefs -> prefs[homeKey] = Json.encodeToString(recipes) }
    }

    suspend fun getHomeRecipes(): List<Recipe> {
        val stored = dataStore.data.first()[homeKey] ?: return emptyList()
        return try {
            Json.decodeFromString(stored)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
