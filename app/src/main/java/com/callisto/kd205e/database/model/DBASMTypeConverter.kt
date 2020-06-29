package com.callisto.kd205e.database.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DBASMTypeConverter
{
    private val gson = Gson()

    @TypeConverter
    fun fromSetScoreModifier(setAsm: Set<DBAbilityScoreModifier>): String?
    {
        return gson.toJson(setAsm)
    }

    @TypeConverter
    fun toSetScoreModifier(value: String): Set<DBAbilityScoreModifier>?
    {
        val groupListType = object : TypeToken<Set<DBAbilityScoreModifier?>?>() {}.type

        val setAsm: Set<DBAbilityScoreModifier> = gson.fromJson(value, groupListType)

        return setAsm
    }
}