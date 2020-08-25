package com.callisto.kd205e.database.converters

import androidx.room.TypeConverter
import com.callisto.kd205e.database.entities.Attribute
import com.google.gson.Gson

class AttributeListConverter
{
    var gson = Gson()

    @TypeConverter
    fun someObjectListToString(someObjects: List<Attribute>?): String?
    {
        return gson.toJson(someObjects)
    }
}