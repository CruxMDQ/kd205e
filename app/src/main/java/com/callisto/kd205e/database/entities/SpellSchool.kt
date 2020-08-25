package com.callisto.kd205e.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

private const val DBSpellSchoolsTable = "spell_schools"
private const val schoolName = "school_name"

@Entity(tableName = DBSpellSchoolsTable, indices = [Index(
    value = [schoolName],
    unique = true
)])
data class SpellSchool
    (
    @PrimaryKey(autoGenerate = true)
    var schoolId: Long = 0L,

    @ColumnInfo(name = schoolName)
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