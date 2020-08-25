package com.callisto.kd205e.database.entities

import androidx.room.*

private const val DBSpellsTable = "spells"
private const val pkSchool = "schoolId"
private const val pkSpell = "spellId"
private const val fkSchoolId = "school_id"
private const val spellName = "name"
private const val spellLevel = "level"
private const val spellDuration = "duration"
private const val spellDescription = "description"

@Entity(tableName = DBSpellsTable,
    indices = [
        Index(
            value = [pkSpell]
        ),
        Index(
            value = [fkSchoolId]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = SpellSchool::class,
            parentColumns = [pkSchool],
            childColumns = [fkSchoolId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Spell
    (
    @PrimaryKey(autoGenerate = true)
    var spellId: Long = 0L,

    @ColumnInfo(name = fkSchoolId)     // race_id
    var schoolId: Long = 0L,

    @ColumnInfo(name = spellName)   // character_name
    var name: String = "",

    @ColumnInfo(name = spellLevel)
    var level: Int = 0,

    @ColumnInfo(name = spellDuration)
    var duration: String = "",

    @ColumnInfo(name = spellDescription)
    var description: String = ""
)
{
    override fun toString(): String
    {
        return name
    }
}