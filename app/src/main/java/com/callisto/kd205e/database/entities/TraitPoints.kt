package com.callisto.kd205e.database.entities

import androidx.room.*

private const val DBTPTable = "traits_points"

private const val abilityPicks = "ability_picks"
private const val pointsPerPick = "points_per_pick"

private const val pkTrait = "traitId"
private const val fkTraitId = "trait_id"

@Entity(tableName = DBTPTable,
    indices = [
        Index(
            value = [fkTraitId]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = Trait::class,
            parentColumns = [pkTrait],    // Primary Key on parent entity
            childColumns = [fkTraitId],     // Target column on this entity
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TraitPoints
(
    @PrimaryKey
    @ColumnInfo(name = fkTraitId)
    var traitId: Long = 0L,

    @ColumnInfo(name = abilityPicks)
    var picks: Int = 0,

    @ColumnInfo(name = pointsPerPick)
    var points: Int = 0
)