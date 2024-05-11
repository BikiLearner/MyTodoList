package com.example.mytodolist.database

import androidx.lifecycle.LiveData
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TodoRepo(private val todoDao: TodoDao) {

    val readAllTodo: Flow<List<TodoDataClass>> = todoDao.getAllTodos()

    suspend fun createOrUpdate(todoDataClass: TodoDataClass) {
        todoDao.insertOrUpdate(todoDataClass)
    }

    suspend fun deleteTodo(todoDataClass: TodoDataClass) {
        todoDao.deleteTodoById(todoDataClass)
    }

    fun getTotalTaskSize():LiveData<Int> {
        return todoDao.getTotalTaskSize()
    }

    fun getTaskByDateIsNotComplete(date: String,isCompleted: Boolean):Flow<List<TodoDataClass>>{
        return todoDao.getTaskByDateIsNotComplete(date,isCompleted =isCompleted )
    }

    fun getTotalTaskSizeByDate(date: String): LiveData<Int>{
        return todoDao.getTotalTaskSizeByDate(date)
    }


    fun getTotalTaskSizeCompletedByDate(isCompleted: Boolean,date: String): LiveData<Int>{
        return todoDao.getTotalTaskSizeCompletedByDate(isCompleted,date)
    }

    fun getTotalTaskSizeCompleted(isCompleted: Boolean): LiveData<Int> {
        return todoDao.getTotalTaskSizeCompleted(isCompleted)
    }

    fun getTaskByID(id: Int): LiveData<TodoDataClass> {
        return todoDao.getTaskByID(id)
    }

    fun getTaskByDate(date: String): Flow<List<TodoDataClass>> {
        return todoDao.getTaskByDate(date)
    }

    fun getTasksByDateAscending(): Flow<List<TodoDataClass>> {
        return todoDao.getTaskByDateAsen()
    }

    fun getTasksByDateDescending(): Flow<List<TodoDataClass>> {
        return todoDao.getTasksByDateDescending()
    }

    fun getCompletedTasks(isCompleted: Boolean): Flow<List<TodoDataClass>> {
        return todoDao.getCompletedTask(isCompleted)
    }

}
