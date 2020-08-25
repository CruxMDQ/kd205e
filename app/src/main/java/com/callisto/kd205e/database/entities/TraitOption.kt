package com.callisto.kd205e.database.entities

import androidx.room.*

private const val DBTOTable = "traits_options"

private const val pkRowId = "row_id"

private const val pkTrait = "traitId"

private const val fkTraitId = "trait_id"
private const val fkOptionId = "option_id"

@Entity(tableName = DBTOTable,
    indices = [
        Index(
            value = [pkRowId, fkTraitId, fkOptionId]
        ),
        Index(
            value = [fkTraitId]
        ),
        Index(
            value = [fkOptionId]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = Trait::class,
            parentColumns = [pkTrait],
            childColumns = [fkTraitId],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Trait::class,
            parentColumns = [pkTrait],
            childColumns = [fkOptionId],
            onDelete = ForeignKey.CASCADE
        )
    ]
    )
data class TraitOption
    (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = pkRowId)
    var rowId: Long = 0L,

    @ColumnInfo(name = fkTraitId)
    var traitId: Long = 0L,

    @ColumnInfo(name = fkOptionId)
    var optionId: Long = 0L
)
{
    constructor(traitId: Long, optionId: Long) : this()
    {
        this.traitId = traitId
        this.optionId = optionId
    }
}