package com.example.mytodolist.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.adapter.TaskItemRecyclerView
import com.example.mytodolist.database.dataClass.TodoCategory
import com.example.mytodolist.database.dataClass.TodoDataClass

import com.example.mytodolist.functionallity.scheduleAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(private val repo: TodoRepo) : ViewModel() {


    private val readAllData: Flow<List<TodoDataClass>> = repo.readAllTodo
    private val readAllTodoCategory: Flow<List<TodoCategory>> = repo.readAllTodoCategory
    fun getAllData(data: (List<TodoDataClass>) -> Unit) {
        viewModelScope.launch {
            readAllData.collect {
                data(it)
            }
        }
    }

    fun createTodoCategoryIDAndInsert(
        categoryName: String,
        todoId: Long,
        taskName: String,
        taskDesc: String,
        date: Date,
        startTime: Time,
        endTime: Time,
        context:Context,
        categoryId: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val time = System.currentTimeMillis()
            val last4Digits = time.toString().takeLast(4)
            val notificationId = last4Digits.toInt()

            if(categoryId==(-1).toLong()){
                val todoDataClass = TodoDataClass(
                    todoId = todoId,
                    taskName=taskName,
                    taskDesc=taskDesc,
                    date=date,
                    startTime=startTime,
                    endTime=endTime,
                    categoryID = repo.insertCategory(
                        TodoCategory(categoryID = 0,categoryName=categoryName)
                    ),
                    uniqueNotificationID = notificationId
                )
                addOrUpdateTodo(todoDataClass,context)
            }else{
                val todoDataClass = TodoDataClass(
                    todoId = todoId,
                    taskName = taskName,
                    taskDesc = taskDesc,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    categoryID = categoryId,
                    uniqueNotificationID = notificationId
                )
                addOrUpdateTodo(todoDataClass, context)

            }
        }
    }

    fun getAllTodoCategory(data: (List<TodoCategory>) -> Unit) {
        viewModelScope.launch {
            readAllTodoCategory.collect {
                data(it)
            }
        }
    }

    fun getTodosByCategory(categoryId: Long, data: (List<TodoDataClass>) -> Unit) {
        viewModelScope.launch {
            repo.getTodosByCategory(categoryId).collect {
                data(it)
            }
        }
    }


    fun getAllCategoryNames(data: (List<String>) -> Unit) {
        viewModelScope.launch {
            repo.getAllCategoryNames().collect {
                data(it)
            }
        }
    }
    fun getCategoryById(categoryId: Long):LiveData <TodoCategory>{
        return repo.getCategoryById(categoryId)
    }

    fun getCategoryIDByName(categoryName: String):LiveData<Long>{
        return repo.getCategoryIDByName(categoryName)
    }
    fun insertCategory(todoCategory: TodoCategory) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertCategory(todoCategory)
        }
    }

    fun deleteCategory(todoCategory: TodoCategory) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteCategory(todoCategory)
        }
    }


    fun getTasksByDateAscending(data: (List<TodoDataClass>) -> Unit) {
        viewModelScope.launch {
            repo.getTasksByDateAscending().collect {
                data(it)
            }
        }
    }

    fun getTasksByDateDescending(data: (List<TodoDataClass>) -> Unit) {
        viewModelScope.launch {
            repo.getTasksByDateDescending().collect {
                data(it)
            }
        }
    }

    fun getCompletedTasks(isComplete: Boolean, data: (List<TodoDataClass>) -> Unit) {
        viewModelScope.launch {
            repo.getCompletedTasks(isComplete).collect {
                data(it)
            }
        }
    }


    fun getTaskByID(id: Long): LiveData<TodoDataClass> {
        return repo.getTaskByID(id)
    }

    fun getTotalTaskSize(): LiveData<Int> {
        return repo.getTotalTaskSize()
    }

    fun getTotalTaskSizeByDate(date: Date): LiveData<Int> {
        return repo.getTotalTaskSizeByDate(Converter().fromDate(date))
    }

    fun getTaskByDateIsNotComplete(
        date: Date,
        isCompleted: Boolean,
        data: (List<TodoDataClass>) -> Unit
    ) {
        viewModelScope.launch {
            repo.getTaskByDateIsNotComplete(Converter().fromDate(date), isCompleted = isCompleted)
                .collect {
                    data(it)
                }
        }
    }


    fun getTotalTaskSizeCompletedByDate(isCompleted: Boolean, date: Date): LiveData<Int> {
        return repo.getTotalTaskSizeCompletedByDate(isCompleted, Converter().fromDate(date))
    }

    fun getTotalTaskSizeCompleted(isCompleted: Boolean): LiveData<Int> {
        return repo.getTotalTaskSizeCompleted(isCompleted)
    }

    fun getTaskByDate(date: Date, data: (List<TodoDataClass>) -> Unit) {
        viewModelScope.launch {
            repo.getTaskByDate(Converter().fromDate(date)).collect {
                data(it)
            }
        }
    }

    fun addOrUpdateTodo(todoDataClass: TodoDataClass, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repo.createOrUpdate(todoDataClass)
            Log.e("TheNewTodoID", "addOrUpdateTodo: $id")
            if(id!=-1L){
                scheduleAlarm(context,todoDataClass.copy(
                    todoId = id
                ))
            }
        }
    }

    fun deleteTodo(todoDataClass: TodoDataClass) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteTodo(todoDataClass)
        }
    }

    fun displayDataRv(
        todoAllData: List<TodoDataClass>,
        recyclerView: RecyclerView,
        context: Context
    ) {
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )
        val adapter = TaskItemRecyclerView(context, todoAllData, { todoDelete ->
            deleteTodo(todoDelete)
        }, { todoUpdateClass ->
            val updatedTask = todoUpdateClass.copy(isComplete = true)
            addOrUpdateTodo(updatedTask, context)
        })
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

    }

}