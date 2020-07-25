package com.callisto.kd205e.database.model

import androidx.room.*

private const val DBAttributesTable = "attributes"
private const val DBCharacterTable = "characters"
private const val DBSpeciesTable = "races"
private const val DBTraitsTable = "traits"
private const val DBASTable = "characters_attributes"
private const val DBCTATable = "characters_traits_attributes"
private const val DBASMTable = "races_attributes"
private const val DBRTTable = "races_traits"
private const val DBTPTable = "traits_points"

private const val attributeName = "attribute_name"
private const val characterName = "character_name"
private const val speciesAdj = "race_adjective"
private const val speciesName = "race_name"
private const val traitName = "trait_name"

private const val abilityPicks = "ability_picks"
private const val pointsPerPick = "points_per_pick"

private const val pkRowId = "row_id"
private const val pkAttribute = "attributeId"
private const val pkCharacter = "characterId"
private const val pkSpecies = "raceId"
private const val pkTrait = "traitId"

private const val fkAttributeId = "attribute_id"
private const val fkCharacterId = "character_id"
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
            entity = DBTrait::class,
            parentColumns = [pkTrait],
            childColumns = [fkTraitId],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DBRace::class,
            parentColumns = [pkSpecies],
            childColumns = [fkSpeciesId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBRaceTrait
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

@Entity(tableName = DBTPTable,
    indices = [
        Index(
            value = [fkTraitId]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = DBTrait::class,
            parentColumns = [pkTrait],    // Primary Key on parent entity
            childColumns = [fkTraitId],     // Target column on this entity
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBTraitPoints
(
    @PrimaryKey
    @ColumnInfo(name = fkTraitId)
    var traitId: Long = 0L,

    @ColumnInfo(name = abilityPicks)
    var picks: Int = 0,

    @ColumnInfo(name = pointsPerPick)
    var points: Int = 0
)

@Entity(tableName = DBTraitsTable)
data class DBTrait
(
    @PrimaryKey(autoGenerate = true)
    var traitId: Long = 0L,

    @ColumnInfo(name = traitName)
    var name: String = ""
)

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
            entity = DBAttribute::class,
            parentColumns = [pkAttribute],
            childColumns = [fkAttributeId],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DBCharacter::class,
            parentColumns = [pkCharacter],
            childColumns = [fkCharacterId],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DBTrait::class,
            parentColumns = [pkTrait],
            childColumns = [fkTraitId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBCharacterAttributeTrait
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

@Entity(tableName = DBSpeciesTable)
data class DBRace
(
    @PrimaryKey(autoGenerate = true)
    var raceId: Long = 0L,

    @ColumnInfo(name = speciesName)
    var name: String = "",

    @ColumnInfo(name = speciesAdj)
    var adjective: String = ""
//    ,
//    @TypeConverters(DBASMTypeConverter::class)
//    val scores: Set<DBAbilityScoreModifier>
)
{
    constructor(name: String) : this()
    {
        this.name = name
    }

    override fun toString(): String
    {
        return name
    }
}

@Entity(tableName = DBAttributesTable, indices = [Index(value = [attributeName], unique = true)])
data class DBAttribute
(
    @PrimaryKey(autoGenerate = true)
    var attributeId: Long = 0L,

    @ColumnInfo(name = attributeName)
    var name: String = ""
)
{
    constructor(name: String) : this()
    {
        this.name = name
    }

    override fun toString(): String
    {
        return name
    }
}

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
            entity = DBAttribute::class,
            parentColumns = [pkAttribute],
            childColumns = [fkAttributeId],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DBRace::class,
            parentColumns = [pkSpecies],
            childColumns = [fkSpeciesId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBAbilityScoreModifier
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
            entity = DBRace::class,
            parentColumns = [pkSpecies],
            childColumns = [fkSpeciesId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBCharacter
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
            entity = DBAttribute::class,
            parentColumns = [pkAttribute],
            childColumns = [fkAttributeId],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DBCharacter::class,
            parentColumns = [pkCharacter],
            childColumns = [fkCharacterId],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBAbilityScore
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