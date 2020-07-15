package com.callisto.kd205e.database.model

import androidx.room.DatabaseView

@DatabaseView("SELECT races.raceId, races.race_name, races.race_adjective, row_id, attribute_name AS name, value FROM races INNER JOIN races_attributes ON races_attributes.race_id = races.raceId INNER JOIN attributes ON races_attributes.attribute_id = attributes.attributeId ORDER BY races.race_name ASC")
data class RacialAttributes(
    val raceId: Long,
    val race_name: String,
    val race_adjective: String,
    val row_id: Long,
    val name: String?,
    val value: Int
)

@DatabaseView("SELECT characters.characterId, characters.character_name, characters.race_id, attributes.attributeId, attributes.attribute_name, characters_attributes.value FROM characters INNER JOIN attributes ON characters_attributes.attribute_id = attributes.attributeId INNER JOIN characters_attributes ON characters_attributes.character_id = characters.characterId")
data class CharacterAbilityScores(
    val characterId: Long,
    val character_name: String,
    val raceId: Long,
    val attributeId: Long,
    val attribute_name: String,
    val value: Int
)
