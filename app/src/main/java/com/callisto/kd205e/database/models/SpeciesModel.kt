package com.callisto.kd205e.database.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.callisto.kd205e.database.entities.Species

private const val pkRowId = "row_id"
private const val colName = "name"
private const val colValue = "value"

private const val fkTraitId = "trait_id"

data class SpeciesModel
(
    @Embedded
    var race: Species,
    var abilityModifiers: List<AbilityScoreModifier?>
)
{
    lateinit var traitModels: List<TraitModel?>

    constructor(param1: Species, param2: List<AbilityScoreModifier?>, param3: List<TraitModel?>): this(param1, param2)
    {
        this.traitModels = param3
    }

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
    lateinit var race : Species

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

data class TraitModel
(
    @ColumnInfo(name = fkTraitId)
    var traitId: Long,

    @ColumnInfo(name = colName)
    var name: String,

    @ColumnInfo(name = colValue)
    var value: Int
)
{
    override fun toString(): String
    {
        return if (value != 0)
        {
            "$name ($value)"
        } else
        {
            name
        }
    }
}