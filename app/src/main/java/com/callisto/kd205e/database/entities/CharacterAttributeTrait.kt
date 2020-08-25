package com.callisto.kd205e.database.entities

import androidx.room.*

private const val DBCTATable = "characters_traits_attributes"

private const val pkRowId = "row_id"
private const val pkAttribute = "attributeId"
private const val pkCharacter = "characterId"
private const val pkTrait = "traitId"
private const val fkAttributeId = "attribute_id"
private const val fkCharacterId = "character_id"
private const val fkTraitId = "trait_id"

@Entity(tableName = DBCTATable,
    indices = [
        Index(
            value = [pkRowId, fkCharacterId, fkAttributeId, fkTraitId]
        ),
        Index(
            value = [fkCharacterId]
        ),
        Index(
            value = [fkAttributeId]
        ),
        Index(
            value = [fkTraitId]
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
        ),
        ForeignKey(
            entity = Trait::class,
            parentColumns = [pkTrait],
            childColumns = [fkTraitId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CharacterAttributeTrait
(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = pkRowId)
    var modifierId: Long = 0L,

    @ColumnInfo(name = fkCharacterId)
    var fCharacterId: Long = 0L,

    @ColumnInfo(name = fkAttributeId)
    var fAttributeId: Long = 0L,

    @ColumnInfo(name = fkTraitId)
    var fTraitId: Long = 0L,

    var value: Int = 0
)
{
    constructor(fCharacterId: Long, attributeId: Long, traitId: Long, value: Int) : this()
    {
        this.fCharacterId = fCharacterId
        this.fAttributeId = attributeId
        this.fTraitId = traitId
        this.value = value
    }
}