package com.callisto.kd205e.database.model

import androidx.room.ColumnInfo

data class AbilityScore
(
    @ColumnInfo(name = "row_id")
    var modifierId: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "value")
    var value: Int
)
