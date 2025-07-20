package com.example.readstack.roomdatabase

import androidx.room.Dao
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

    // Fixed: Sum ALL currentPages from ALL books (not just finished ones)
    @Query("SELECT SUM(currentPage) FROM books WHERE currentPage > 0")
    suspend fun getTotalPagesReadTillNow(): Int?

    // Fixed: Sum currentPages for books updated this month (any shelf)
    @Query("""
        SELECT SUM(currentPage) FROM books 
        WHERE currentPage > 0 
        AND strftime('%Y-%m', datetime(updatedAt/1000, 'unixepoch')) = strftime('%Y-%m', 'now')
    """)
    suspend fun getPagesReadThisMonth(): Int?

    // Alternative: If you only want to count pages from finished books
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