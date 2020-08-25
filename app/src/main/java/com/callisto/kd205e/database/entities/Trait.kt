package com.callisto.kd205e.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

private const val DBTraitsTable = "traits"
private const val traitName = "trait_name"

@Entity(tableName = DBTraitsTable)
data class Trait
(
    @PrimaryKey(autoGenerate = true)
    var traitId: Long = 0L,

    @ColumnInfo(name = traitName)
    var name: String = ""
)
{
    override fun toString(): String
    {
        return name
    }
}