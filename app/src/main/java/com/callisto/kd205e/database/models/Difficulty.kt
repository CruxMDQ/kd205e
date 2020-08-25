package com.callisto.kd205e.database.models

data class Difficulty(val title: String, var value: Int)
{
    override fun toString(): String
    {
        return title
    }
}