package com.callisto.kd205e.database.model

import androidx.room.ColumnInfo

data class AbilityScoreModifier
(
    @ColumnInfo(name = "row_id")
    var modifierId: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "value")
    var value: Int
)
{
    override fun toString(): String
    {
        return "+$value to $name"
    }
}