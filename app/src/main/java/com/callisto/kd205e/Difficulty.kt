package com.callisto.kd205e

data class Difficulty(val title: String, val value: Int)
{
    override fun toString(): String
    {
        return title
    }

    fun getStringValue(): String
    {
        return value.toString()
    }
}
