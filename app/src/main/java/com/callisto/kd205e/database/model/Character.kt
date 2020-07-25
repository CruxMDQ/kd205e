package com.callisto.kd205e.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

private const val pkAttributeId = "attributeId"
private const val colName = "attribute_name"
private const val colValue = "value"

data class Character(
    @Embedded
    var character: DBCharacter,

    var species: Species? = null,

    var abilityScores: List<AbilityScore>?,

    var traitPoints: List<AbilityTraitModifier>?
)
{
    fun getFinalScore(parameter: String) : Int
    {
        return getBaseScore(parameter) + getModifierScore(parameter) + getTraitModifiers(parameter)
    }

    fun getBaseScore(parameter: String) : Int
    {
        return abilityScores!!.find { it.name == parameter}?.value ?: 0
    }

    fun getModifierScore(parameter: String) : Int
    {
        return species!!.abilityModifiers.find { it!!.name == parameter }?.value ?: 0
    }

    fun getTraitModifiers(parameter: String) : Int
    {
        var result = 0

        for (item in traitPoints!!)
        {
            if (item.attributeName == parameter)
            {
                result += item.value
            }
        }

        return result
    }
}

data class AbilityScore
(
    @ColumnInfo(name = pkAttributeId)
    var attributeId: Long,

    @ColumnInfo(name = colName)
    var name: String,

    @ColumnInfo(name = colValue)
    var value: Int
)

class CharacterScorePair
{
    @Embedded
    lateinit var character : DBCharacter

    @Embedded
    var abilityScore: AbilityScore? = null
}

class AbilityTraitModifier
(
    @ColumnInfo(name = colName)
    var attributeName: String,

    @ColumnInfo(name = colValue)
    var value: Int
)