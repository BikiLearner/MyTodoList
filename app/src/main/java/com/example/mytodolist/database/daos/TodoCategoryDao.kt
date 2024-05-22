package com.example.mytodolist.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.mytodolist.database.dataClass.TodoCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoCategoryDao {
    @Upsert
    suspend fun insertCategory(todoCategory: TodoCategory): Long
    @Delete
    suspend fun deleteCategory(todoCategory: TodoCategory): Int
    @Query("SELECT * FROM TODOCategory")
    fun getCategories(): Flow<List<TodoCategory>>
    @Query("SELECT categoryName FROM TODOCategory")
    fun getAllCategoryNames():Flow<List<String>>
    @Query("SELECT * FROM todocategory WHERE categoryID = :categoryId")
    fun getCategoryById(categoryId: Long):LiveData<TodoCategory>

    @Query("SELECT categoryID FROM todocategory WHERE categoryName = :categoryName")
    fun getCategoryIDByName(categoryName: String): LiveData<Long>
}