package com.example.readstack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.readstack.roomdatabase.Quote
import com.example.readstack.roomdatabase.QuoteDao
import kotlinx.coroutines.launch

class QuoteViewModel(private val quoteDao: QuoteDao) : ViewModel() {
    fun insertQuote(quote: Quote) {
        viewModelScope.launch {
            quoteDao.insertQuote(quote)
        }
    }
}

class QuoteViewModelFactory(private val quoteDao: QuoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuoteViewModel(quoteDao) as T
    }
}
