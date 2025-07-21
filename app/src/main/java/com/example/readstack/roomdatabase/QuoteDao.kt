package com.example.readstack.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
@Dao
interface QuoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: Quote)

    @Query("SELECT * FROM quotes WHERE bookId = :bookId ORDER BY timestamp DESC")
    suspend fun getQuotesByBookId(bookId: String): List<Quote>

    @Query("SELECT * FROM quotes WHERE bookId = :bookId ORDER BY timestamp DESC")
    fun getQuotesFlowByBookId(bookId: String): Flow<List<Quote>>

    @Update
    suspend fun updateQuote(quote: Quote)

    @Delete
    suspend fun deleteQuote(quote: Quote)

    @Query("SELECT * FROM quotes ORDER BY timestamp DESC")
    fun getAllQuotes(): Flow<List<Quote>>

    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun getTotalQuotesCount(): Int

    @Query("SELECT * FROM quotes WHERE tags LIKE '%' || :tag || '%' ORDER BY timestamp DESC")
    fun getQuotesByTag(tag: String): Flow<List<Quote>>

    @Query("DELETE FROM quotes WHERE bookId = :bookId")
    suspend fun deleteAllQuotesForBook(bookId: String)


}