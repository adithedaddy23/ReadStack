package com.example.readstack.api.roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDateTime

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey(autoGenerate = true) val quoteId: Long = 0,
    val bookId: String,
    val quoteText: String,
    val noteText: String?,
    @TypeConverters(TagsConverter::class) val tags: List<String>,
    val timestamp: LocalDateTime
)

class TagsConverter {
    @TypeConverter
    fun fromList(tags: List<String>): String = tags.joinToString(",")
    @TypeConverter
    fun toList(tagsString: String): List<String> = tagsString.split(",").filter { it.isNotBlank() }
}

