package com.callisto.kd205e.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.callisto.kd205e.database.model.*

@Database(entities = [
    DBAttribute::class,
    DBAbilityScore::class,
    DBAbilityScoreModifier::class,
    DBCharacter::class,
    DBRace::class],
    views = [
    RacialAttributes::class],
    version = 8, exportSchema = false)
@TypeConverters(DBASMTypeConverter::class)
abstract class Kd205eDatabase : RoomDatabase()
{
    abstract val raceDao: RaceDao

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