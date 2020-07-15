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

    var abilityScores: List<AbilityScore>?
)
{
    fun getFinalScore(parameter: String) : Int
    {
        return getBaseScore(parameter) + getModifierScore(parameter)
    }

    fun getBaseScore(parameter: String) : Int
    {
        return abilityScores!!.find { it.name == parameter}?.value ?: 0
    }

    fun getModifierScore(parameter: String) : Int
    {
        return species!!.abilityModifiers.find { it!!.name == parameter }?.value ?: 0
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
