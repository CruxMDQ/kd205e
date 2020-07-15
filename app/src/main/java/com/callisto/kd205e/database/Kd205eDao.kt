package com.callisto.kd205e.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.callisto.kd205e.database.model.*

@Dao
interface Kd205eDao
{
    companion object{
        const val queryForAbilityScoreModifier = "SELECT modifierId, attribute_name AS name, value FROM races INNER JOIN race_attributes ON race_attributes.fRaceId = races.raceId INNER JOIN attributes ON race_attributes.fAttributeId = attributes.attributeId WHERE races.raceId = :id"
    }

    @Transaction
    @Query("SELECT * FROM Races")
    fun getRacesWithScores(): List<DBRace>

    @Query("SELECT * FROM attributes")
    fun getAllAttributes(): LiveData<List<DBAttribute>>

    @Query("SELECT * FROM attributes")
    fun checkAllAttributes(): List<DBAttribute>

    // If the @Insert method receives only 1 parameter, it can return a long, which is the new
    // rowId for the inserted item.
    // If the parameter is an array or a collection, it should return long[] or List<Long> instead.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAttribute(attribute: DBAttribute): Long

    @Query("SELECT * FROM attributes ORDER BY attributeId LIMIT 1")
    fun getSingleAttribute(): DBAttribute?

    @Query("SELECT * FROM attributes WHERE attributeId = :id" )
    fun getSingleAttribute(id: Int): DBAttribute?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAbilityScoreModifier(abilityScoreModifier: DBAbilityScoreModifier)

    // In order for these queries to work, the returned column names must EXACTLY MATCH the fields
    // on the object class listed as return!
    @Query("SELECT row_id, attribute_name AS name, value FROM races INNER JOIN races_attributes ON races_attributes.race_id = races.raceId INNER JOIN attributes ON races_attributes.attribute_id = attributes.attributeId WHERE races.raceId = :id")
    fun getAbilityScoreModifiers(id: Long): List<AbilityScoreModifier>

    @Query("SELECT races.raceId, races.race_name, races.race_adjective, row_id, attribute_name AS name, value FROM races INNER JOIN races_attributes ON races_attributes.race_id = races.raceId INNER JOIN attributes ON races_attributes.attribute_id = attributes.attributeId")
    fun getRacesAndAbilityModifiers(): List<RaceModifierPair>

    @Query("SELECT * FROM RacialAttributes")
    fun queryRacialAttributesView(): List<RaceModifierPair>

    @Query("SELECT * FROM CharacterAbilityScores")
    fun queryCharacterAbilityScoresView(): List<CharacterScorePair>

    @Query("SELECT * FROM races WHERE raceId = :id")
    fun getSingleRace(id: Long): DBRace?

    @Insert
    fun createNewCharacter(character: DBCharacter) : Long

    @Update
    fun updateCharacter(character: DBCharacter)

    @Query("SELECT * FROM characters WHERE characterId = :id")
    fun getSingleCharacter(id: Long): DBCharacter?

    @Query("SELECT attributes.attributeId, attributes.attribute_name, characters_attributes.value FROM characters_attributes INNER JOIN characters ON characters.characterId = characters_attributes.character_id INNER JOIN attributes ON attributes.attributeId = characters_attributes.attribute_id WHERE characters_attributes.character_id = :id")
    fun getScoresForCharacter(id: Long): List<AbilityScore>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacterScore(abilityScore: DBAbilityScore) : Long

    @Update
    fun updateCharacterScore(abilityScore: DBAbilityScore)
}
