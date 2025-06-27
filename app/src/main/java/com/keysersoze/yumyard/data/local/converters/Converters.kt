package com.keysersoze.yumyard.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>): String = Gson().toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromPairList(value: List<Pair<String, String>>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toPairList(value: String): List<Pair<String, String>> {
        val listType = object : TypeToken<List<Pair<String, String>>>() {}.type
        return Gson().fromJson(value, listType)
    }
}