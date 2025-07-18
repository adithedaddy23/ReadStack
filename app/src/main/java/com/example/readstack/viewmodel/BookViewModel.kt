package com.example.readstack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readstack.api.ApiResponse
import com.example.readstack.api.Author
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

    private val _bookDetailsResult = MutableStateFlow<NetworkResponseClass<CombinedBookDetails>>(NetworkResponseClass.Error(""))
    val bookDetailsResult : StateFlow<NetworkResponseClass<CombinedBookDetails>> = _bookDetailsResult

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
                println("DEBUG: About to fetch combined book details for workKey: $workKey")

                // Ensure the workKey starts with "/works/"
                val formattedKey = if (!workKey.startsWith("/works/")) {
                    "/works/$workKey"
                } else {
                    workKey
                }

                // Fetch both APIs concurrently
                val detailsDeferred = async { bookApi.getBookDetails(formattedKey) }
                val searchDeferred = async {
                    // Extract just the work ID for search
                    val workId = formattedKey.substringAfterLast("/")
                    bookApi.searchBooks(workId, 1)
                }

                val details = detailsDeferred.await()
                val searchResult = searchDeferred.await()

                // Combine the data
                val combinedData = CombinedBookDetails(
                    key = details.key,
                    title = details.title,
                    description = details.description,
                    covers = details.covers,
                    subjects = details.subjects,
                    firstPublishDate = null,
                    // Get author names from search result
                    authorNames = searchResult.docs?.firstOrNull()?.authorName ?: emptyList(),
                    // Keep original author structure as backup
                    authors = details.authors
                )

                println("DEBUG: Combined API calls successful")
                _bookDetailsResult.value = NetworkResponseClass.Success(combinedData)
            } catch (e: Exception) {
                println("DEBUG: Combined API calls failed with exception: ${e.javaClass.simpleName}")
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

// Data class to hold combined information from both APIs
data class CombinedBookDetails(
    val key: String?,
    val title: String?,
    val description: Any?, // Can be String or Map
    val covers: List<Int>?,
    val subjects: List<String>?,
    val firstPublishDate: String?,
    val authorNames: List<String>, // From search API
    val authors: List<Author>? // From details API as backup
)