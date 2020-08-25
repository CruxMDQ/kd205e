package com.callisto.kd205e.database.entities

import androidx.room.*

private const val DBCTTable = "characters_traits"

private const val pkRowId = "row_id"

private const val pkCharacter = "characterId"
private const val pkTrait = "traitId"

private const val fkCharacterId = "character_id"
private const val fkTraitId = "trait_id"

@Entity(tableName = DBCTTable,
    indices = [
        Index(
            value = [pkRowId, fkCharacterId, fkTraitId]
        ),
        Index(
            value = [fkCharacterId]
        ),
        Index(
            value = [fkTraitId]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = [pkCharacter],
            childColumns = [fkCharacterId],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Trait::class,
            parentColumns = [pkTrait],
            childColumns = [fkTraitId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CharacterTrait
    (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = pkRowId)
    var rowId: Long = 0L,

    @ColumnInfo(name = fkCharacterId)
    var fCharacterId: Long = 0L,

    @ColumnInfo(name = fkTraitId)
    var fTraitId: Long = 0L,

    var value: Int = 0
)