package com.example.readstack.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE shelf = :shelf")
    fun getBooksByShelf(shelf: String): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: String): Book?

    @Query("SELECT * FROM books WHERE shelf = 'finished' AND strftime('%Y-%m', datetime(updatedAt/1000, 'unixepoch')) = strftime('%Y-%m', 'now')")
    suspend fun getBooksReadThisMonth(): List<Book>

    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookFlow(bookId: String): Flow<Book?>

    // DELETE FUNCTIONALITY
    @Delete
    suspend fun deleteBook(book: Book)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: String)

    // FAVORITES FUNCTIONALITY
    @Query("SELECT * FROM books WHERE isFavorite = 1")
    fun getFavoriteBooks(): Flow<List<Book>>

    @Query("UPDATE books SET isFavorite = :isFavorite WHERE id = :bookId")
    suspend fun updateBookFavoriteStatus(bookId: String, isFavorite: Boolean)

    // Existing queries...
    @Query("SELECT SUM(currentPage) FROM books WHERE currentPage > 0")
    suspend fun getTotalPagesReadTillNow(): Int?

    @Query("""
        SELECT SUM(currentPage) FROM books 
        WHERE currentPage > 0 
        AND strftime('%Y-%m', datetime(updatedAt/1000, 'unixepoch')) = strftime('%Y-%m', 'now')
    """)
    suspend fun getPagesReadThisMonth(): Int?

    @Query("SELECT SUM(currentPage) FROM books WHERE shelf = 'finished' AND currentPage > 0")
    suspend fun getTotalPagesFromFinishedBooks(): Int?

    @Query("""
        SELECT SUM(currentPage) FROM books 
        WHERE shelf = 'finished' 
        AND currentPage > 0
        AND strftime('%Y-%m', datetime(updatedAt/1000, 'unixepoch')) = strftime('%Y-%m', 'now')
    """)
    suspend fun getPagesFromFinishedBooksThisMonth(): Int?

    @Query("SELECT COUNT(*) FROM books WHERE shelf = 'finished'")
    suspend fun getTotalBooksRead(): Int
}