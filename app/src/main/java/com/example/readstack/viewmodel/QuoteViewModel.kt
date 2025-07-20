package com.example.readstack.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.readstack.roomdatabase.Quote
import com.example.readstack.roomdatabase.QuoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class QuoteViewModel(private val quoteDao: QuoteDao) : ViewModel() {

    // Cache for all quotes StateFlow to avoid recreating it
    private var _allQuotesFlow: StateFlow<List<Quote>>? = null

    // Insert quote with proper dispatcher
    suspend fun insertQuote(quote: Quote) = withContext(Dispatchers.IO) {
        quoteDao.insertQuote(quote)
    }

    suspend fun getQuotesByBookId(bookId: String): List<Quote> {
        return withContext(Dispatchers.IO) {
            quoteDao.getQuotesByBookId(bookId)
        }
    }

    // Fixed implementation - cache the StateFlow to avoid recreating it
    fun getQuotesFlow(bookId: String): StateFlow<List<Quote>> {
        return quoteDao.getQuotesFlowByBookId(bookId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // Get all quotes as Flow - this is what the Analytics screen needs
    fun getAllQuotes(): StateFlow<List<Quote>> {
        if (_allQuotesFlow == null) {
            _allQuotesFlow = quoteDao.getAllQuotes()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
        }
        return _allQuotesFlow!!
    }

    // Get total quotes count for analytics
    suspend fun getTotalQuotesCount(): Int = withContext(Dispatchers.IO) {
        quoteDao.getTotalQuotesCount()
    }

    // Get quotes by tag
    fun getQuotesByTag(tag: String): StateFlow<List<Quote>> {
        return quoteDao.getQuotesByTag(tag)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // Get random quotes from all quotes
    suspend fun getRandomQuotes(count: Int = 4): List<Quote> = withContext(Dispatchers.IO) {
        val allQuotes = quoteDao.getAllQuotes().first() // Get current list
        allQuotes.shuffled().take(count)
    }

    // Make deleteQuote suspend function for consistency
    suspend fun deleteQuote(quote: Quote) = withContext(Dispatchers.IO) {
        quoteDao.deleteQuote(quote)
    }

    // Make updateQuote suspend function for consistency
    suspend fun updateQuote(quote: Quote) = withContext(Dispatchers.IO) {
        quoteDao.updateQuote(quote)
    }

    // Create quote with current timestamp
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createQuote(
        bookId: String,
        quoteText: String,
        noteText: String? = null,
        tags: List<String> = emptyList()
    ) {
        val quote = Quote(
            bookId = bookId,
            quoteText = quoteText,
            noteText = noteText,
            tags = tags,
            timestamp = LocalDateTime.now()
        )
        insertQuote(quote)
    }
}

class QuoteViewModelFactory(private val quoteDao: QuoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuoteViewModel(quoteDao) as T
    }
}
