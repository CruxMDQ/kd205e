package com.callisto.kd205e.database.entities

import androidx.room.*

private const val DBRTTable = "races_traits"

private const val pkRowId = "row_id"
private const val pkTrait = "traitId"
private const val pkSpecies = "raceId"
private const val fkSpeciesId = "race_id"
private const val fkTraitId = "trait_id"

@Entity(tableName = DBRTTable,
    indices = [
        Index(
            value = [pkRowId, fkTraitId, fkSpeciesId]
        ),
        Index(
            value = [fkSpeciesId]
        ),
        Index(
            value = [fkTraitId]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = Trait::class,
            parentColumns = [pkTrait],
            childColumns = [fkTraitId],
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
data class RaceTrait
    (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = pkRowId)
    var rowId: Long = 0L,

    @ColumnInfo(name = fkSpeciesId)
    var fRaceId: Long = 0L,

    @ColumnInfo(name = fkTraitId)
    var fTraitId: Long = 0L,

    var value: Int = 0
)
{
    constructor(raceId: Long, traitId: Long, value: Int) : this()
    {
        this.fRaceId = raceId
        this.fTraitId = traitId
        this.value = value
    }

    constructor(raceId: Long, traitId: Long) : this()
    {
        this.fRaceId = raceId
        this.fTraitId = traitId
    }
}