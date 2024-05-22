package com.example.mytodolist.database

import androidx.lifecycle.LiveData
import com.example.mytodolist.database.daos.TodoCategoryDao
import com.example.mytodolist.database.daos.TodoDao
import com.example.mytodolist.database.dataClass.TodoCategory
import com.example.mytodolist.database.dataClass.TodoDataClass
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepo @Inject constructor(
    private val todoDao: TodoDao,
    private val todoCategoryDao: TodoCategoryDao
) {

    val readAllTodo: Flow<List<TodoDataClass>> = todoDao.getAllTodos()
    val readAllTodoCategory: Flow<List<TodoCategory>> = todoCategoryDao.getCategories()



    suspend fun insertCategory(todoCategory: TodoCategory):Long{
        return todoCategoryDao.insertCategory(todoCategory)
    }
    suspend fun deleteCategory(todoCategory: TodoCategory):Int{
        return todoCategoryDao.deleteCategory(todoCategory)
    }
    suspend fun createOrUpdate(todoDataClass: TodoDataClass) {
        todoDao.insertOrUpdate(todoDataClass)
    }


    fun getTodosByCategory(categoryId:Long):Flow<List<TodoDataClass>>{
       return todoDao.getTodosByCategory(categoryId)
    }
    suspend fun updateIsComplete(todoId: Long, isCompleteValue: Boolean){
        todoDao.updateIsComplete(todoId,isCompleteValue)
    }
    suspend fun deleteTodo(todoDataClass: TodoDataClass) {
        todoDao.deleteTodoById(todoDataClass)
    }

    fun getCategoryById(categoryId: Long): LiveData<TodoCategory> {
        return todoCategoryDao.getCategoryById(categoryId)
    }

    fun getCategoryIDByName(categoryName: String): LiveData<Long>{
       return todoCategoryDao.getCategoryIDByName(categoryName)
    }
    fun isCategoryPresent(categoryId: Long):Boolean{
        val category=todoCategoryDao.getCategoryById(categoryId).value
        return category!=null
    }
    fun getAllCategoryNames():Flow<List<String>>{
        return todoCategoryDao.getAllCategoryNames()
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

    fun getTaskByID(id: Long): LiveData<TodoDataClass> {
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
