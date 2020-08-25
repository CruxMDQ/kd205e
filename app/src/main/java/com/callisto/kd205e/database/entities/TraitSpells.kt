package com.callisto.kd205e.database.entities

import androidx.room.*

private const val DBTSTable = "traits_spells"

private const val pkRowId = "row_id"
private const val pkTrait = "traitId"
private const val fkTraitId = "trait_id"

private const val pkSpell = "spellId"
private const val fkSpellId = "spell_id"

@Entity(tableName = DBTSTable,
    indices = [
        Index(
            value = [pkRowId, fkTraitId, fkSpellId]
        ),
        Index(
            value = [fkTraitId]
        ),
        Index(
            value = [fkSpellId]
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
            entity = Spell::class,
            parentColumns = [pkSpell],
            childColumns = [fkSpellId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TraitSpells
(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = pkRowId)
    var scoreId: Long = 0L,

    @ColumnInfo(name = fkTraitId)
    var fTraitId: Long = 0L,

    @ColumnInfo(name = fkSpellId)
    var fSpellId: Long = 0L
)
{
    constructor(traitId: Long, spellId: Long) : this()
    {
        this.fTraitId = traitId
        this.fSpellId = spellId
    }
}