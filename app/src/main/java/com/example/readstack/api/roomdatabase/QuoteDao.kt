package com.example.readstack.api.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuoteDao {
    @Insert
    suspend fun insertQuote(quote: Quote)

    @Query("SELECT * FROM quotes WHERE bookId = :bookId")
    suspend fun getQuotesByBook(bookId: String): List<Quote>

    @Query("SELECT * FROM quotes WHERE strftime('%Y-%m', timestamp) = strftime('%Y-%m', 'now')")
    suspend fun getQuotesThisMonth(): List<Quote>
}