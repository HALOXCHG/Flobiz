package com.flobiz.challenge.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flobiz.challenge.models.Converters
import com.flobiz.challenge.models.Questions
import com.flobiz.challenge.models.QuestionsDao

const val DATABASE_NAME = "question_database"

@Database(
    entities = [Questions::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RoomDb : RoomDatabase() {
    //    abstract fun textDao(): TextDao
    abstract fun questionDao(): QuestionsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: RoomDb? = null

        fun getDatabase(context: Context): RoomDb {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDb::class.java,
                    DATABASE_NAME
                )
                    .allowMainThreadQueries().build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}