package com.example.readstack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readstack.api.ApiResponse
import com.example.readstack.api.NetworkResponseClass
import com.example.readstack.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel: ViewModel() {
    private val bookApi = RetrofitInstance.bookApi
    private val _bookResult = MutableStateFlow<NetworkResponseClass<ApiResponse>>(NetworkResponseClass.Error(""))
    val bookResult : StateFlow<NetworkResponseClass<ApiResponse>> = _bookResult


    fun getData(name: String) {
        _bookResult.value = NetworkResponseClass.loading
        viewModelScope.launch {
            try {
                val response = bookApi.searchBooks(name,20);
                _bookResult.value = NetworkResponseClass.Success(response)
            } catch (e: Exception) {
                _bookResult.value = NetworkResponseClass.Error(e.message.toString())
            }
        }
    }
}