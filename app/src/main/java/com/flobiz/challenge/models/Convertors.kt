package com.flobiz.challenge.models

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun listToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) =
        Gson().fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun ownerToJson(value: Owner) =
        Gson().toJson(value)

    @TypeConverter
    fun jsonToOwner(value: String) =
        Gson().fromJson(value, Owner::class.java)
}
