package com.example.readstack.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.readstack.api.BookApi
import com.example.readstack.api.RetrofitInstance
import com.example.readstack.roomdatabase.Book
import com.example.readstack.roomdatabase.BookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.Response

data class NetworkResponseClass<T>(
    val status: Status,
    val data: T?,
    val message: String?
) {
    enum class Status { LOADING, SUCCESS, ERROR }

    companion object {
        val loading = NetworkResponseClass(Status.LOADING, null, null)
        fun <T> success(data: T) = NetworkResponseClass(Status.SUCCESS, data, null)
        fun <T> error(message: String) = NetworkResponseClass<T>(Status.ERROR, null, message)
    }
}

data class BookStorageResult(
    val status: Status,
    val message: String?
) {
    enum class Status { SUCCESS, ERROR }
}

class BookStorageViewModel(
    private val bookDao: BookDao,
    private val bookApi: BookApi = RetrofitInstance.bookApi
) : ViewModel() {

    private val _storageResult = MutableStateFlow(BookStorageResult(BookStorageResult.Status.SUCCESS, null))
    val storageResult: StateFlow<BookStorageResult> = _storageResult

    val currentlyReadingBooks: StateFlow<List<Book>> = bookDao.getBooksByShelf("currently_reading")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keeps the stream alive for 5s after the UI stops listening
            initialValue = emptyList() // The list is empty at the very beginning
        )

    val wantToReadBooks: StateFlow<List<Book>> = bookDao.getBooksByShelf("want_to_read")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val finishedBooks: StateFlow<List<Book>> = bookDao.getBooksByShelf("finished")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteBooks: StateFlow<List<Book>> = bookDao.getFavoriteBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveBookFromApi(workKey: String, shelf: String) {
        viewModelScope.launch {
            try {
                // Ensure workKey is properly formatted
                val formattedKey = if (!workKey.startsWith("/works/")) {
                    "/works/$workKey"
                } else {
                    workKey
                }

                // Fetch book details from API
                val response = bookApi.getBookDetails(formattedKey)

                // Convert API response to Book entity
                val book = Book(
                    id = response.key ?: formattedKey,
                    title = response.title ?: "Unknown Title",
                    coverUrl = response.covers?.firstOrNull()?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" },
                    shelf = shelf,
                    currentPage = 0,
                    updatedAt = System.currentTimeMillis(),
                    isFavorite = false
                )

                // Insert or update book in database
                bookDao.insertBook(book)
                _storageResult.value = BookStorageResult(BookStorageResult.Status.SUCCESS, "Book saved successfully")

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.UnknownHostException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Request timed out"
                    is retrofit2.HttpException -> "HTTP ${e.code()}: ${e.message()}"
                    else -> "Failed to save book: ${e.message}"
                }
                _storageResult.value = BookStorageResult(BookStorageResult.Status.ERROR, errorMessage)
            }
        }
    }

    fun updateBookShelf(bookId: String, newShelf: String) {
        viewModelScope.launch {
            try {
                val book = bookDao.getBookById(bookId)
                    ?: throw IllegalStateException("Book with id $bookId not found")

                val updatedBook = book.copy(
                    shelf = newShelf,
                    updatedAt = System.currentTimeMillis()
                )
                bookDao.updateBook(updatedBook)
                _storageResult.value = BookStorageResult(BookStorageResult.Status.SUCCESS, "Shelf updated successfully")
            } catch (e: Exception) {
                _storageResult.value = BookStorageResult(BookStorageResult.Status.ERROR, "Failed to update shelf: ${e.message}")
            }
        }
    }

    // DELETE BOOK FUNCTIONALITY
    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            try {
                bookDao.deleteBookById(bookId)
                _storageResult.value = BookStorageResult(BookStorageResult.Status.SUCCESS, "Book deleted successfully")
            } catch (e: Exception) {
                _storageResult.value = BookStorageResult(BookStorageResult.Status.ERROR, "Failed to delete book: ${e.message}")
            }
        }
    }

    // FAVORITES FUNCTIONALITY
    fun toggleBookFavorite(bookId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                bookDao.updateBookFavoriteStatus(bookId, isFavorite)
                val message = if (isFavorite) "Added to favorites" else "Removed from favorites"
                _storageResult.value = BookStorageResult(BookStorageResult.Status.SUCCESS, message)
            } catch (e: Exception) {
                _storageResult.value = BookStorageResult(BookStorageResult.Status.ERROR, "Failed to update favorite: ${e.message}")
            }
        }
    }

    suspend fun getBookById(bookId: String): Book? {
        return bookDao.getBookById(bookId)
    }

    fun updateBookProgress(bookId: String, totalPages: Int?, pagesRead: Int?) {
        viewModelScope.launch {
            val book = bookDao.getBookById(bookId) ?: return@launch
            val updated = book.copy(
                totalPages = totalPages ?: book.totalPages,
                currentPage = pagesRead ?: book.currentPage,
                updatedAt = System.currentTimeMillis()
            )
            bookDao.updateBook(updated)
        }
    }

    fun getBookFlow(bookId: String): Flow<Book?> {
        return bookDao.getBookFlow(bookId) // This should return a Flow that emits whenever the book data changes
    }

    suspend fun getPagesReadThisMonth(): Int {
        return bookDao.getPagesReadThisMonth() ?: 0
    }

    suspend fun getTotalPagesReadTillNow(): Int {
        return bookDao.getTotalPagesReadTillNow() ?: 0
    }

    suspend fun getTotalBooksRead(): Int {
        return bookDao.getTotalBooksRead()
    }
    suspend fun getBooksReadThisMonth(): List<Book> {
        return bookDao.getBooksReadThisMonth()
    }

}

class BookStorageViewModelFactory(
    private val bookDao: BookDao,
    private val bookApi: BookApi = RetrofitInstance.bookApi
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        println("DEBUG: Creating BookStorageViewModel with BookDao: $bookDao")
        if (modelClass.isAssignableFrom(BookStorageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookStorageViewModel(bookDao, bookApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}