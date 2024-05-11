package com.example.mytodolist.database

import com.example.mytodolist.adapter.TaskItemRecyclerView
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date

class TodoViewModel(application: Application):AndroidViewModel(application) {

    private val repo:TodoRepo
    private val readAllData:Flow<List<TodoDataClass>>
    init {
        val todoDao=TodoDatabase.getDatabase(application).todoDao()
        repo= TodoRepo(todoDao)
        readAllData=repo.readAllTodo
    }
    fun getAllData(data:(List<TodoDataClass>)->Unit){
        viewModelScope.launch {
            readAllData.collect{
                data(it)
            }
        }
    }
    fun getTasksByDateAscending(data:(List<TodoDataClass>)->Unit){
        viewModelScope.launch {
           repo.getTasksByDateAscending().collect{
               data(it)
           }
        }
    }

    fun getTasksByDateDescending(data:(List<TodoDataClass>)->Unit){
        viewModelScope.launch {
            repo.getTasksByDateDescending().collect{
                data(it)
            }
        }
    }
    fun getCompletedTasks(isComplete:Boolean,data:(List<TodoDataClass>)->Unit){
        viewModelScope.launch {
            repo.getCompletedTasks(isComplete).collect{
                data(it)
            }
        }
    }



    fun getTaskByID(id: Int): LiveData<TodoDataClass>{
        return repo.getTaskByID(id)
    }
    fun getTotalTaskSize():  LiveData<Int> {
        return repo.getTotalTaskSize()
    }

    fun getTotalTaskSizeByDate(date: Date): LiveData<Int>{
        return repo.getTotalTaskSizeByDate(Converter().fromDate(date))
    }

    fun getTaskByDateIsNotComplete(date: Date,isCompleted: Boolean,data:(List<TodoDataClass>)->Unit){
        viewModelScope.launch {
            repo.getTaskByDateIsNotComplete(Converter().fromDate(date), isCompleted = isCompleted)
                .collect{
                data(it)
            }
        }
    }


    fun getTotalTaskSizeCompletedByDate(isCompleted: Boolean,date: Date): LiveData<Int>{
        return repo.getTotalTaskSizeCompletedByDate(isCompleted,Converter().fromDate(date))
    }

    fun getTotalTaskSizeCompleted(isCompleted: Boolean):  LiveData<Int> {
        return repo.getTotalTaskSizeCompleted(isCompleted)
    }
    fun getTaskByDate(date:Date,data:(List<TodoDataClass>)->Unit){
        viewModelScope.launch {
            repo.getTaskByDate(Converter().fromDate(date)).collect{
                data(it)
            }
        }
    }
    fun addOrUpdateTodo(todoDataClass: TodoDataClass){
        viewModelScope.launch(Dispatchers.IO){
            repo.createOrUpdate(todoDataClass)
        }
    }
    fun deleteTodo(todoDataClass: TodoDataClass){
        viewModelScope.launch(Dispatchers.IO){
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
        },{todoUpdateClass->
            val updatedTask = todoUpdateClass.copy(isComplete = true)
            addOrUpdateTodo(updatedTask)
        })
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

    }

}