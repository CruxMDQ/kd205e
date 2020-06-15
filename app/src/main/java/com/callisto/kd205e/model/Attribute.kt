package com.callisto.kd205e.model

data class Attribute(val name: String, var value: Int)
{
    override fun toString(): String
    {
        return name
    }
}