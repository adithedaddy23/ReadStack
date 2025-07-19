package com.example.readstack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.readstack.roomdatabase.Quote
import com.example.readstack.roomdatabase.QuoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuoteViewModel(private val quoteDao: QuoteDao) : ViewModel() {

    // Insert quote with proper dispatcher
    suspend fun insertQuote(quote: Quote) = withContext(Dispatchers.IO) {
        quoteDao.insertQuote(quote)
    }

    suspend fun getQuotesByBookId(bookId: String): List<Quote> {
        return quoteDao.getQuotesByBookId(bookId)
    }

    // This implementation is correct and will emit updates when the underlying data changes.
    // The problematic function in QuoteViewModel
    fun getQuotesFlow(bookId: String): StateFlow<List<Quote>> {
        // This entire chain creates a new StateFlow on every call
        return quoteDao.getQuotesFlowByBookId(bookId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // Make deleteQuote suspend function for consistency
    suspend fun deleteQuote(quote: Quote) = withContext(Dispatchers.IO) {
        quoteDao.deleteQuote(quote)
    }

    // Make updateQuote suspend function for consistency
    suspend fun updateQuote(quote: Quote) = withContext(Dispatchers.IO) {
        quoteDao.updateQuote(quote)
    }
}

class QuoteViewModelFactory(private val quoteDao: QuoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuoteViewModel(quoteDao) as T
    }
}
