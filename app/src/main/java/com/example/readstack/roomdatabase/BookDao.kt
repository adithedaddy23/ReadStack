package com.example.readstack.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE shelf = :shelf")
    suspend fun getBooksByShelf(shelf: String): List<Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Query("SELECT * FROM books WHERE shelf = 'finished' AND strftime('%Y-%m', 'now') = strftime('%Y-%m', updated_at)")
    suspend fun getBooksReadThisMonth() : List<Book>
}