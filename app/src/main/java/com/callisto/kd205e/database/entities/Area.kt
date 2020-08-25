package com.callisto.kd205e.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

private const val DBAreasTable = "areas"
private const val areaName = "area_name"

@Entity(tableName = DBAreasTable, indices = [Index(
    value = [areaName],
    unique = true
)])
data class Area
    (
    @PrimaryKey(autoGenerate = true)
    var areaId: Long = 0L,

    @ColumnInfo(name = areaName)
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