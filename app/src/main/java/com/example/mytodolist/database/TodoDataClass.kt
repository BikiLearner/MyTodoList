package com.example.mytodolist.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity(tableName = "TodoListTable")
data class TodoDataClass(
    @PrimaryKey(autoGenerate = true)
    val todoId:Int,
    val taskName:String,
    val taskDesc:String,
    val date: Date,
    val startTime: Time,
    val endTime:Time,
    val isComplete:Boolean=false
    )
