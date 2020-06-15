package com.callisto.kd205e.model

// TODO Implement a way to deal with overlapping skill proficiencies
// If a character would gain the same proficiency from two different sources, he or she can choose a
// different proficiency of the same kind (skill or tool) instead.
data class Skill(val name: String)
{
    override fun toString(): String
    {
        return name
    }
}