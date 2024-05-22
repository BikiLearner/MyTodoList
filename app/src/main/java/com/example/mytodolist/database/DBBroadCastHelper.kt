package com.example.mytodolist.database

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DBBroadCastHelper @Inject constructor (
    private val todoRepo: TodoRepo

) {
    fun setMarkCompleted(todoID:Long):Boolean{
        CoroutineScope(Dispatchers.IO).launch{
            todoRepo.updateIsComplete(todoID,true)
        }
        Log.e("Marked complete","Marked")
        return true
    }
}