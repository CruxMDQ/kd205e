package com.callisto.kd205e.database.entities

import androidx.room.*

private const val DBASMTable = "races_attributes"

private const val pkRowId = "row_id"
private const val pkAttribute = "attributeId"
private const val pkSpecies = "raceId"

private const val fkAttributeId = "attribute_id"
private const val fkSpeciesId = "race_id"

// Source: https://itnext.io/how-to-create-m-n-relationship-with-room-and-kotlin-ddbdebf0ee38
@Entity(tableName = DBASMTable,
    indices = [
        Index(
            value = [pkRowId, fkSpeciesId, fkAttributeId]
        ),
        Index(
            value = [fkAttributeId]
        ),
        Index(
            value = [fkSpeciesId]
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
            entity = Species::class,
            parentColumns = [pkSpecies],
            childColumns = [fkSpeciesId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AbilityScoreModifier
(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = pkRowId)
    var modifierId: Long = 0L,

    @ColumnInfo(name = fkSpeciesId)
    var fRaceId: Long = 0L,

    @ColumnInfo(name = fkAttributeId)
    var fAttributeId: Long = 0L,

    var value: Int = 0
)
{
    constructor(raceId: Long, attributeId: Long, value: Int) : this()
    {
        this.fRaceId = raceId
        this.fAttributeId = attributeId
        this.value = value
    }
}