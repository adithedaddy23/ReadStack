package com.example.readstack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readstack.api.ApiResponse
import com.example.readstack.api.BookDetailResponse
import com.example.readstack.api.NetworkResponseClass
import com.example.readstack.api.RetrofitInstance
import com.example.readstack.api.SubjectResponse
import com.example.readstack.api.Work
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel: ViewModel() {
    private val bookApi = RetrofitInstance.bookApi
    private val _bookResult = MutableStateFlow<NetworkResponseClass<ApiResponse>>(NetworkResponseClass.Error(""))
    val bookResult : StateFlow<NetworkResponseClass<ApiResponse>> = _bookResult

    private val _bookDetailsResult = MutableStateFlow<NetworkResponseClass<BookDetailResponse>>(NetworkResponseClass.Error(""))
    val bookDetailsResult : StateFlow<NetworkResponseClass<BookDetailResponse>> = _bookDetailsResult

    private val _subjectBooks = MutableStateFlow<NetworkResponseClass<SubjectResponse>>(NetworkResponseClass.Error(""))
    val subjectBooks: StateFlow<NetworkResponseClass<SubjectResponse>> = _subjectBooks

    val genres = listOf(
        "fiction",
        "fantasy",
        "science",
        "romance",
        "history",
        "mystery",
        "horror",
        "thriller",
        "adventure",
        "biography",
        "science fiction",
        "poetry",
        "drama",
        "philosophy",
        "self help",
        "psychology",
        "health",
        "humor",
        "music",
        "sports",
        "technology",
        "education",
        "politics"
    )

    private val _genreBooks = MutableStateFlow<Map<String, List<Work>>>(emptyMap())
    val genreBooks: StateFlow<Map<String, List<Work>>> = _genreBooks

    private val _genreBooksLoading = MutableStateFlow(false)
    val genreBooksLoading: StateFlow<Boolean> = _genreBooksLoading

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

    fun getBooksBySubject(subject: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _subjectBooks.value = NetworkResponseClass.loading
            try {
                val response = bookApi.getBooksBySubject(subject.lowercase())
                _subjectBooks.value = NetworkResponseClass.Success(response)
            } catch (e: Exception) {
                _subjectBooks.value = NetworkResponseClass.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchGenreBooks() {
        viewModelScope.launch {
            _genreBooksLoading.value = true
            val resultMap = mutableMapOf<String, List<Work>>()

            // Fetch genres in parallel for better performance
            val deferredResults = genres.map { genre ->
                async {
                    try {
                        val response = bookApi.getBooksBySubject(genre, 10)
                        genre to response.works
                    } catch (e: Exception) {
                        genre to emptyList<Work>()
                    }
                }
            }

            // Wait for all requests to complete
            deferredResults.awaitAll().forEach { (genre, works) ->
                if (works.isNotEmpty()) {
                    resultMap[genre] = works
                }
            }

            _genreBooks.value = resultMap
            _genreBooksLoading.value = false
        }
    }
}