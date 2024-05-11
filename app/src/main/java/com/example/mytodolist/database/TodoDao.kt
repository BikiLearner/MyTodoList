package com.example.mytodolist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TodoDao {
    @Upsert
    suspend fun insertOrUpdate(todo: TodoDataClass)

    @Delete
    suspend fun deleteTodoById(todo: TodoDataClass)

    @Query("SELECT * FROM TodoListTable WHERE todoId = :id")
    fun getTaskByID(id: Int): LiveData<TodoDataClass>



    @Query("SELECT * FROM TodoListTable")
    fun getAllTodos(): Flow<List<TodoDataClass>>


    @Query("SELECT * FROM TodoListTable WHERE date=:date")
    fun getTaskByDate(date: String):Flow<List<TodoDataClass>>

    @Query("SELECT * FROM TodoListTable WHERE date=:date AND isComplete=:isCompleted")
    fun getTaskByDateIsNotComplete(date: String,isCompleted: Boolean):Flow<List<TodoDataClass>>

    @Query("SELECT COUNT(*) FROM TodoListTable")
    fun getTotalTaskSize(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM TodoListTable WHERE date=:date")
    fun getTotalTaskSizeByDate(date: String): LiveData<Int>


    @Query("SELECT COUNT(*) FROM TodoListTable WHERE isComplete = :isCompleted")
    fun getTotalTaskSizeCompleted(isCompleted: Boolean): LiveData<Int>

    @Query("SELECT COUNT(*) FROM TodoListTable WHERE isComplete = :isCompleted AND date=:date")
    fun getTotalTaskSizeCompletedByDate(isCompleted: Boolean,date: String): LiveData<Int>

    @Query("SELECT * FROM TodoListTable ORDER BY date ASC")
    fun getTaskByDateAsen():Flow<List<TodoDataClass>>

    @Query("SELECT * FROM TodoListTable  ORDER BY date DESC")
    fun getTasksByDateDescending(): Flow<List<TodoDataClass>>

    @Query("SELECT * FROM TodoListTable WHERE isComplete = :isCompleted")
    fun getCompletedTask(isCompleted: Boolean): Flow<List<TodoDataClass>>

}