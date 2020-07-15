package com.callisto.kd205e.database.model

import androidx.room.*

private const val DBAttributesTable = "attributes"
private const val DBCharacterTable = "characters"
private const val DBSpeciesTable = "races"
private const val DBASTable = "characters_attributes"
private const val DBASMTable = "races_attributes"

private const val attributeName = "attribute_name"
private const val characterName = "character_name"
private const val speciesAdj = "race_adjective"
private const val speciesName = "race_name"

private const val pkRowId = "row_id"
private const val pkAttribute = "attributeId"
private const val pkCharacter = "characterId"
private const val pkSpecies = "raceId"

private const val fkAttributeId = "attribute_id"
private const val fkCharacterId = "character_id"
private const val fkSpeciesId = "race_id"

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