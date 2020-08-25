package com.callisto.kd205e.database.entities

import androidx.room.*

private const val DBASTable = "characters_attributes"

private const val pkRowId = "row_id"
private const val pkAttribute = "attributeId"
private const val pkCharacter = "characterId"

private const val fkAttributeId = "attribute_id"
private const val fkCharacterId = "character_id"

@Entity(tableName = DBASTable,
    indices = [
        Index(
            value = [pkRowId, fkCharacterId, fkAttributeId]
        ),
        Index(
            value = [fkAttributeId]
        ),
        Index(
            value = [fkCharacterId]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = Attribute::class,
            parentColumns = [pkAttribute],
            childColumns = [fkAttributeId],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Character::class,
            parentColumns = [pkCharacter],
            childColumns = [fkCharacterId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CharacterAbilityScore
(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = pkRowId)
    var scoreId: Long = 0L,

    @ColumnInfo(name = fkCharacterId)
    var fCharacterId: Long = 0L,

    @ColumnInfo(name = fkAttributeId)
    var fAttributeId: Long = 0L,

    var value: Int = 0
)
{
    constructor(characterId: Long, attributeId: Long, value: Int) : this()
    {
        this.fCharacterId = characterId
        this.fAttributeId = attributeId
        this.value = value
    }
}