package com.example.readstack.api.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReadingSessionDao {
    @Insert
    suspend fun insertSession(session: ReadingSession)

    @Query("SELECT SUM(pagesRead) FROM reading_sessions WHERE bookId = :bookId AND strftime('%Y-%m', timestamp) = strftime('%Y-%m', 'now')")
    suspend fun getPagesReadThisMonth(bookId: String): Int

    @Query("SELECT SUM(pagesRead) FROM reading_sessions WHERE strftime('%Y-%m', timestamp) = strftime('%Y-%m', 'now')")
    suspend fun getTotalPagesReadThisMonth(): Int

    @Query("SELECT DISTINCT bookId FROM reading_sessions WHERE strftime('%Y-%m', timestamp) = strftime('%Y-%m', 'now')")
    suspend fun getBooksReadThisMonth(): List<String>
}