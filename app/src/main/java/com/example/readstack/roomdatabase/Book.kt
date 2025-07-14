package com.example.readstack.roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val shelf: String,
    val totalPages: Int,
    val currentPage: Int = 0
)
