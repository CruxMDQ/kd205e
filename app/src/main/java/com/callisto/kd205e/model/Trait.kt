package com.callisto.kd205e.model

data class Trait(val name: String, val value: Int)
{
    override fun toString(): String
    {
        return name
    }
}