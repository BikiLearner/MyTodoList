package com.example.mytodolist.database.dataClass

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "TODOCategory",indices = [Index(value = ["categoryName"], unique = true)])
data class TodoCategory(
    @PrimaryKey(autoGenerate = true)
    val categoryID: Long,
    val categoryName:String,
)

