package com.example.readstack.roomdatabase

import android.os.Build
import androidx.annotation.RequiresApi
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
    val tags: List<String>,
    val timestamp: LocalDateTime
)

@TypeConverters(TagsConverter::class, LocalDateTimeConverter::class)
class TagsConverter {
    @TypeConverter
    fun fromList(tags: List<String>): String = tags.joinToString(",")

    @TypeConverter
    fun toList(tagsString: String): List<String> = tagsString.split(",").filter { it.isNotBlank() }
}

// You'll also need a LocalDateTime converter
class LocalDateTimeConverter {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }
}

