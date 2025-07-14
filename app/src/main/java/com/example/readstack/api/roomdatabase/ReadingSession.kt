package com.example.readstack.api.roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "reading_sessions")
data class ReadingSession(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0,
    val bookId: String,
    val pagesRead: Int,
    val duration: Long,
    val timestamp: LocalDateTime
)
