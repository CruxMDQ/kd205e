package com.callisto.kd205e

data class Difficulty(val title: String, var value: Int)
{
    override fun toString(): String
    {
        return title
    }
}
