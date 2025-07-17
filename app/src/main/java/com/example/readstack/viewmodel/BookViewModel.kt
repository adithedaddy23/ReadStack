package com.example.readstack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readstack.api.ApiResponse
import com.example.readstack.api.BookDetailResponse
import com.example.readstack.api.NetworkResponseClass
import com.example.readstack.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel: ViewModel() {
    private val bookApi = RetrofitInstance.bookApi
    private val _bookResult = MutableStateFlow<NetworkResponseClass<ApiResponse>>(NetworkResponseClass.Error(""))
    val bookResult : StateFlow<NetworkResponseClass<ApiResponse>> = _bookResult

    private val _bookDetailsResult = MutableStateFlow<NetworkResponseClass<BookDetailResponse>>(NetworkResponseClass.Error(""))
    val bookDetailsResult : StateFlow<NetworkResponseClass<BookDetailResponse>> = _bookDetailsResult

    fun getData(name: String) {
        _bookResult.value = NetworkResponseClass.loading
        viewModelScope.launch {
            try {
                val response = bookApi.searchBooks(name,30);
                _bookResult.value = NetworkResponseClass.Success(response)
            } catch (e: Exception) {
                _bookResult.value = NetworkResponseClass.Error(e.message.toString())
            }
        }
    }

    fun getBookDetails(workKey: String) {
        viewModelScope.launch {
            _bookDetailsResult.value = NetworkResponseClass.loading
            try {
                println("DEBUG: About to call API with workKey: $workKey")

                // Ensure the workKey starts with "/works/"
                val formattedKey = if (!workKey.startsWith("/works/")) {
                    "/works/$workKey"
                } else {
                    workKey
                }

                val result = bookApi.getBookDetails(formattedKey)
                println("DEBUG: API call successful")
                _bookDetailsResult.value = NetworkResponseClass.Success(result)
            } catch (e: Exception) {
                println("DEBUG: API call failed with exception: ${e.javaClass.simpleName}")
                println("DEBUG: Error message: ${e.message}")
                e.printStackTrace()

                val errorMessage = when (e) {
                    is java.net.UnknownHostException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Request timed out"
                    is retrofit2.HttpException -> "HTTP ${e.code()}: ${e.message()}"
                    else -> "Failed: ${e.message}"
                }

                _bookDetailsResult.value = NetworkResponseClass.Error(errorMessage)
            }
        }
    }
}