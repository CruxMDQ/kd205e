package com.callisto.kd205e.database.models

import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.callisto.kd205e.database.entities.Character
import com.callisto.kd205e.database.entities.Trait
import java.lang.StringBuilder

private const val pkAttributeId = "attributeId"
private const val colName = "attribute_name"
private const val colValue = "value"

data class CharacterModel(
    @Embedded
    var character: Character,

    var speciesModel: SpeciesModel? = null,

    var abilityScores: List<AbilityScore>? = null,

    var traitPoints: List<AbilityTraitModifier>? = null,

    var traits: List<Trait>? = null
)
{
    constructor(character: Character, speciesModel: SpeciesModel?)
            : this(character, speciesModel, null, null, null)
    {
        this.character = character
        this.speciesModel = speciesModel
    }

    constructor(character: Character) : this (character, null, null, null, null)
    {
        this.character = character
    }

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
        return speciesModel!!.abilityModifiers.find { it!!.name == parameter }?.value ?: 0
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
    lateinit var character : Character

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