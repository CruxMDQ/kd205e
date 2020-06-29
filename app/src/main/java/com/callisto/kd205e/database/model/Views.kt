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
