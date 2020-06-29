package com.callisto.kd205e.database.model

import androidx.room.Embedded

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