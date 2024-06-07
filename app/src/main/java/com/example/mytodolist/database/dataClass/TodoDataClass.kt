package com.example.mytodolist.database.dataClass

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "TodoListTable",
   indices = [Index(value = ["uniqueNotificationID"], unique = true)]
)
data class TodoDataClass(
    @PrimaryKey(autoGenerate = true)
    val todoId:Long,
    val taskName:String,
    val taskDesc:String,
    val date: Date?,
    val startTime: Long,
    val isComplete:Boolean=false,
    val categoryID:Long,
    val uniqueNotificationID:Int,
    @Embedded
    val repeat:RepeatModel?=null
)
