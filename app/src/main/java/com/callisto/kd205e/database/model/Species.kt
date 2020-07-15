package com.callisto.kd205e.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

private const val pkRowId = "row_id"
private const val colName = "name"
private const val colValue = "value"

data class Species
(
    @Embedded
    val race: DBRace,
    val abilityModifiers: List<AbilityScoreModifier?>
)
{
    override fun toString(): String
    {
        return race.toString()
    }
}

/*
*  class RecipeDayPair {
        @Embedded(prefix = "recipe_")
        lateinit var recipe : Recipe

        @Embedded
        var day : Day? = null
    }*/
class RaceModifierPair
{
    @Embedded
    lateinit var race : DBRace

    @Embedded
    var abilityScoreModifier : AbilityScoreModifier? = null
}

data class AbilityScoreModifier
(
    @ColumnInfo(name = pkRowId)
    var modifierId: Long,

    @ColumnInfo(name = colName)
    var name: String,

    @ColumnInfo(name = colValue)
    var value: Int
)
{
    override fun toString(): String
    {
        return "+$value to $name"
    }
}