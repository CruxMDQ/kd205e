package com.callisto.kd205e.database.entities

import androidx.room.*

private const val DBCharacterTable = "characters"
private const val characterName = "character_name"

private const val pkCharacter = "characterId"
private const val pkSpecies = "raceId"

private const val fkSpeciesId = "race_id"


@Entity(tableName = DBCharacterTable,
    indices = [
        Index(
            value = [pkCharacter]
        ),
        Index(
            value = [fkSpeciesId]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = Species::class,
            parentColumns = [pkSpecies],
            childColumns = [fkSpeciesId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Character
    (
    @PrimaryKey(autoGenerate = true)
    var characterId: Long = 0L,

    @ColumnInfo(name = fkSpeciesId)     // race_id
    var raceId: Long = 0L,

    @ColumnInfo(name = characterName)   // character_name
    var name: String = ""
)
{
    override fun toString(): String
    {
        return name
    }

    constructor(raceId: Long) : this()
    {
        this.raceId = raceId
    }
}