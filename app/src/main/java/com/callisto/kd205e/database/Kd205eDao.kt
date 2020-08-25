package com.callisto.kd205e.database

import androidx.room.*
import com.callisto.kd205e.database.entities.*
import com.callisto.kd205e.database.models.AbilityScore
import com.callisto.kd205e.database.models.RaceModifierPair
import com.callisto.kd205e.database.models.TraitModel

@Dao
interface Kd205eDao
{
    @Query("SELECT * FROM attributes")
    fun getAllAttributes(): List<Attribute>

    // If the @Insert method receives only 1 parameter, it can return a long, which is the new
    // rowId for the inserted item.
    // If the parameter is an array or a collection, it should return long[] or List<Long> instead.

    // In order for these queries to work, the returned column names must EXACTLY MATCH the fields
    // on the object class listed as return!
//    @Query("SELECT row_id, attribute_name AS name, value FROM races INNER JOIN races_attributes ON races_attributes.race_id = races.raceId INNER JOIN attributes ON races_attributes.attribute_id = attributes.attributeId WHERE races.raceId = :id")
//    fun getAbilityScoreModifiers(id: Long): List<AbilityScoreModifier>

    @Query("SELECT * FROM RacialAttributes")
    fun queryRacialAttributesView(): List<RaceModifierPair>

    @Query("SELECT * FROM races WHERE raceId = :id")
    fun getSingleRace(id: Long): Species?

    @Insert
    fun createNewCharacter(character: Character) : Long

    @Update
    fun updateCharacter(character: Character) : Int

    @Query("SELECT * FROM characters WHERE characterId = :id")
    fun getSingleCharacter(id: Long): Character?

    @Query("SELECT attributes.attributeId, attributes.attribute_name, characters_attributes.value FROM characters_attributes INNER JOIN characters ON characters.characterId = characters_attributes.character_id INNER JOIN attributes ON attributes.attributeId = characters_attributes.attribute_id WHERE characters_attributes.character_id = :id")
    fun getScoresForCharacter(id: Long): List<AbilityScore>?

    @Query("SELECT row_id, character_id, attribute_id, value FROM characters_attributes WHERE characters_attributes.character_id = :characterId AND characters_attributes.attribute_id = :attributeId")
    fun getScoreForCharacter(characterId: Long, attributeId: Long) : CharacterAbilityScore

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacterScore(characterAbilityScore: CharacterAbilityScore) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCharacterScore(characterAbilityScore: CharacterAbilityScore) : Int

    @Query("SELECT traits_points.trait_id, traits_points.ability_picks, traits_points.points_per_pick FROM traits_points INNER JOIN races_traits ON races_traits.race_id = :raceId")
    fun getAbilityPicksForRacialTraits(raceId: Long) : List<TraitPoints>

//    @Query("SELECT * FROM traits WHERE traits.traitId = :traitId")
//    fun getTrait(traitId: Long) : Trait
//
//    @Query("SELECT * FROM traits")
//    fun getAllTraits() : List<Trait>

    @Query("SELECT * FROM races_traits WHERE races_traits.race_id = :raceId")
    fun getRaceTraits(raceId: Long) : List<RaceTrait>

    @Query("SELECT trait_id, trait_name AS name, value FROM traits INNER JOIN races_traits WHERE traits.traitId = races_traits.trait_id AND races_traits.race_id = :raceID")
    fun getRacialTraits(raceID: Long) : List<TraitModel>

    @Query("SELECT * FROM characters_traits_attributes WHERE character_id = :characterId")
    fun getCharacterAttributeTraits(characterId: Long) : List<CharacterAttributeTrait>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacterAttributeTrait(characterAttributeTrait: CharacterAttributeTrait) : Long

    @Query("DELETE FROM characters_traits_attributes WHERE character_id = :characterId AND attribute_id = :attributeId AND trait_id = :traitId")
    fun deleteCharacterAttributeTrait(characterId: Long, attributeId: Long, traitId: Long) : Int

//    @Query("SELECT traits_options.row_id, traits_options.trait_id, traits_options.option_id FROM traits_options INNER JOIN traits ON traits_options.trait_id = traits.traitId INNER JOIN races_traits ON races_traits.race_id = :raceId")
//    @Query("SELECT traits.traitId, traits.trait_name FROM traits INNER JOIN traits_options ON traits.traitId = traits_options.option_id INNER JOIN races_traits ON races_traits.trait_id = traits_options.trait_id WHERE races_traits.race_id = :raceId")
    @Query("SELECT traits.traitId, traits.trait_name FROM traits INNER JOIN traits_options ON traits.traitId = traits_options.trait_id INNER JOIN races_traits ON races_traits.trait_id = traits_options.trait_id WHERE races_traits.race_id = :raceId GROUP BY traitId")
    fun getRacialTraitsWithOptions(raceId: Long): List<Trait>

    @Query("SELECT traits.traitId, traits.trait_name FROM traits INNER JOIN traits_options ON traits.traitId = traits_options.option_id INNER JOIN races_traits ON races_traits.trait_id = traits_options.trait_id WHERE races_traits.race_id = :raceId")
    fun getTraitOptionsForRacialTrait(raceId: Long): List<Trait>

//    @Query("SELECT traits.traitId, traits.trait_name FROM traits INNER JOIN traits_options ON traits.traitId = traits_options.option_id INNER JOIN races_traits ON races_traits.trait_id = traits_options.trait_id WHERE traits.traitId = :traitId")
    @Query("SELECT traits.traitId, traits.trait_name FROM traits INNER JOIN traits_options ON traits.traitId = traits_options.option_id INNER JOIN races_traits ON races_traits.trait_id = traits_options.trait_id WHERE races_traits.trait_id = :traitId")
    fun getRacialTraitOptions(traitId: Long): List<Trait>

    @Query("SELECT COUNT(*) FROM characters_traits")
    fun getLastCharacterTraitId(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacterTrait(characterTrait: CharacterTrait) : Long

//    @Query("DELETE FROM characters_traits WHERE character_id = :characterId AND trait_id = :traitId")
//    fun deleteCharacterTraitKey(characterId: Long, traitId: Long)

//    @Query("DELETE FROM characters_traits WHERE trait_id IN (SELECT traits.traitId FROM traits INNER JOIN traits_options ON traits.traitId = traits_options.option_id INNER JOIN races_traits ON races_traits.trait_id = traits_options.trait_id WHERE races_traits.race_id != :raceId)")
    @Query("DELETE FROM characters_traits WHERE character_id = :characterId")
    fun deleteCharacterTraitKeysByCharacter(characterId: Long) : Int

//    @Query("SELECT * FROM traits INNER JOIN characters_traits ON traits.traitId = characters_traits.trait_id WHERE characters_traits.character_id = :characterId")
    @Query("SELECT traitId, trait_name FROM traits INNER JOIN characters_traits ON traits.traitId = characters_traits.trait_id WHERE characters_traits.character_id = :characterId")
    fun getCharacterTraits(characterId: Long): List<Trait>

    @Query("SELECT row_id, character_id, trait_id, value FROM characters_traits WHERE character_id = :characterId")
    fun getCharacterTraitKeys(characterId: Long): List<CharacterTrait>
}
