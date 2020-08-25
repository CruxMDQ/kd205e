package com.callisto.kd205e.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.callisto.kd205e.database.models.*
import com.callisto.kd205e.database.entities.*
import com.callisto.kd205e.database.entities.CharacterAbilityScore
import com.callisto.kd205e.database.entities.AbilityScoreModifier
import com.callisto.kd205e.database.entities.Species
import com.callisto.kd205e.database.entities.Character

@Database(entities = [
        Attribute::class,
        CharacterAbilityScore::class,
        AbilityScoreModifier::class,
        Character::class,
        Species::class,
        RaceTrait::class,
        Trait::class,
        TraitPoints::class,
        CharacterAttributeTrait::class,
        Area::class,
        SpellSchool::class,
        Spell::class,
        TraitSpells::class,
        TraitOption::class,
        CharacterTrait::class
    ],
    views = [
        CharacterAbilityScores::class,
        RacialAttributes::class
    ],
    version = 18, exportSchema = true)
@TypeConverters(Kd205eTypeConverters::class)
abstract class Kd205eDatabase : RoomDatabase()
{
    abstract val dao: Kd205eDao

    // Allows clients to access methods for creating/getting DB w/o instantiating class
    companion object
    {
        @Volatile
        private var INSTANCE: Kd205eDatabase? = null

        fun getInstance(context: Context): Kd205eDatabase
        {
            synchronized(this)
            {
                var instance = INSTANCE

                if (instance == null)
                {
//                    Room.databaseBuilder(appContext, AppDatabase.class, "Sample.db")
//                        .createFromAsset("database/myapp.db")
//                        .build()

                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        Kd205eDatabase::class.java,
                        "kd205e_database"
                    )
                        .createFromAsset("kd205e.db")
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}