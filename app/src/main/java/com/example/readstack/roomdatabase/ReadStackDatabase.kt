package com.example.readstack.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [Book::class, ReadingSession::class, Quote::class], version = 4, exportSchema = false)
@TypeConverters(TagsConverter::class, DateTimeConverter::class)
abstract class ReadStackDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun readingSessionDao(): ReadingSessionDao
    abstract fun quoteDao(): QuoteDao

    companion object {
        @Volatile
        private var INSTANCE: ReadStackDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): ReadStackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReadStackDatabase::class.java,
                    "readstack_database"
                )
                    .fallbackToDestructiveMigration() // Add this for version upgrade
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}