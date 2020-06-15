package com.callisto.kd205e.model

data class Character
    (val name: String,
     val race: Race,
     val background: Background,
     var classes: MutableSet<PlayableClass>,
     var abilityScores: MutableSet<Attribute>,
     var skills: MutableSet<Skill>)
{
    override fun toString(): String
    {
        return if (classes.size == 1)
        {
            name + ", " + race.adjective + " " + classes.first().toString()
        }
        else
        {
            name + ", " + race.adjective + " multiclass character"
        }
    }
}