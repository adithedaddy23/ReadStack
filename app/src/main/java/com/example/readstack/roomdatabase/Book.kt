package com.example.readstack.roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: String,
    val title: String,
    val coverUrl: String?,
    val shelf: String,
    val currentPage: Int = 0,
    val totalPages: Int? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)