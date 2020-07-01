package com.callisto.kd205e.database.model

import androidx.room.*

// Source: https://itnext.io/how-to-create-m-n-relationship-with-room-and-kotlin-ddbdebf0ee38
@Entity(tableName = "races_attributes",
    indices = [
        Index(
            value = ["row_id", "race_id", "attribute_id"]
        ),
        Index(
            value = ["attribute_id"]
        ),
        Index(
            value = ["race_id"]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = DBAttribute::class,
            parentColumns = ["attributeId"],
            childColumns = ["attribute_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DBRace::class,
            parentColumns = ["raceId"],
            childColumns = ["race_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBAbilityScoreModifier
(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "row_id")
    var modifierId: Long = 0L,

    @ColumnInfo(name = "race_id")
    var fRaceId: Long = 0L,

    @ColumnInfo(name = "attribute_id")
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

@Entity(tableName = "characters_attributes",
    indices = [
        Index(
            value = ["row_id", "character_id", "attribute_id"]
        ),
        Index(
            value = ["attribute_id"]
        ),
        Index(
            value = ["character_id"]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = DBAttribute::class,
            parentColumns = ["attributeId"],
            childColumns = ["attribute_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DBCharacter::class,
            parentColumns = ["characterId"],
            childColumns = ["character_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBAbilityScore
(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "row_id")
    var scoreId: Long = 0L,

    @ColumnInfo(name = "character_id")
    var fCharacterId: Long = 0L,

    @ColumnInfo(name = "attribute_id")
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

@Entity(tableName = "characters",
    foreignKeys = [
        ForeignKey(
            entity = DBRace::class,
            parentColumns = ["raceId"],
            childColumns = ["race_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBCharacter
    (
    @PrimaryKey(autoGenerate = true)
    var characterId: Long = 0L,

    @ColumnInfo(name = "race_id")
    var raceId: Long = 0L,

    @ColumnInfo(name = "character_name")
    var name: String = ""
)
{
    override fun toString(): String
    {
        return name
    }
}

@Entity(tableName = "attributes", indices = [Index(value = ["attribute_name"], unique = true)])
data class DBAttribute
    (
    @PrimaryKey(autoGenerate = true)
    var attributeId: Long = 0L,

    @ColumnInfo(name = "attribute_name")
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

@Entity(tableName = "races")
data class DBRace
    (
    @PrimaryKey(autoGenerate = true)
    var raceId: Long = 0L,

    @ColumnInfo(name = "race_name")
    var name: String = "",

    @ColumnInfo(name = "race_adjective")
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
