package com.callisto.kd205e.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

private const val DBAttributesTable = "attributes"
private const val attributeName = "attribute_name"

@Entity(tableName = DBAttributesTable, indices = [Index(
    value = [attributeName],
    unique = true
)])
data class Attribute
(
    @PrimaryKey(autoGenerate = true)
    var attributeId: Long = 0L,

    @ColumnInfo(name = attributeName)
    var name: String = ""
)
{
    constructor(name: String) : this()
    {
        this.name = name
    }

    override fun toString(): String
    {
        return name
    }
}