package com.callisto.kd205e.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

private const val DBSpeciesTable = "races"
private const val speciesAdj = "race_adjective"
private const val speciesName = "race_name"

@Entity(tableName = DBSpeciesTable)
data class Species
(
    @PrimaryKey(autoGenerate = true)
    var raceId: Long = 0L,

    @ColumnInfo(name = speciesName)
    var name: String = "",

    @ColumnInfo(name = speciesAdj)
    var adjective: String = ""
//    ,
//    @TypeConverters(DBASMTypeConverter::class)
//    val scores: Set<DBAbilityScoreModifier>
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