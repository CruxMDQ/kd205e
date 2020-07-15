package com.callisto.kd205e.database.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Kd205eTypeConverters
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

    @TypeConverter
    fun fromSetAbilityScores(scores: Set<AbilityScore>) : String?
    {
        return gson.toJson(scores)
    }

    @TypeConverter
    fun toSetAbilityScores(value: String): Set<AbilityScore>?
    {
        val groupListType = object : TypeToken<Set<AbilityScore?>?>() {}.type

        val set: Set<AbilityScore> = gson.fromJson(value, groupListType)

        return set
    }

//    @TypeConverter
//    fun fromCharacter(character: Character) : String?
//    {
//        return gson.toJson(character)
//    }
//
//    @TypeConverter
//    fun toCharacter(value: String): Character?
//    {
//        val groupListType = object : TypeToken<Character?>() {}.type
//
//        val result: Character = gson.fromJson(value, groupListType)
//
//        return result
//    }
}