package com.callisto.kd205e.database.model

import androidx.room.Embedded

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
    var abilityScoreModifier : AbilityScoreModifier ? = null
}