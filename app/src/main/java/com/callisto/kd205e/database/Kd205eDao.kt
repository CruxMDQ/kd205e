package com.callisto.kd205e.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.callisto.kd205e.database.model.*

@Dao
interface Kd205eDao
{
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

    @Query("SELECT row_id, character_id, attribute_id, value FROM characters_attributes WHERE characters_attributes.character_id = :characterId AND characters_attributes.attribute_id = :attributeId")
    fun getScoreForCharacter(characterId: Long, attributeId: Long) : DBAbilityScore

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacterScore(abilityScore: DBAbilityScore) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCharacterScore(abilityScore: DBAbilityScore) : Int

    @Query("SELECT traits_points.trait_id, traits_points.ability_picks, traits_points.points_per_pick FROM traits_points INNER JOIN races_traits ON races_traits.race_id = :raceId")
    fun getAbilityPicksForRacialTraits(raceId: Long) : List<DBTraitPoints>

    @Query("SELECT * FROM traits WHERE traits.traitId = :traitId")
    fun getTrait(traitId: Long) : DBTrait

    @Query("SELECT * FROM characters_traits_attributes WHERE character_id = :characterId")
    fun getCharacterAttributeTraits(characterId: Long) : List<DBCharacterAttributeTrait>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacterAttributeTrait(characterAttributeTrait: DBCharacterAttributeTrait) : Long

    @Query("DELETE FROM characters_traits_attributes WHERE character_id = :characterId AND attribute_id = :attributeId AND trait_id = :traitId")
    fun qDeleteCharacterAttributeTrait(characterId: Long, attributeId: Long, traitId: Long) : Int
}
