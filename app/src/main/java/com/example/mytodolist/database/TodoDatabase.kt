package com.example.mytodolist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mytodolist.database.daos.TodoCategoryDao
import com.example.mytodolist.database.daos.TodoDao
import com.example.mytodolist.database.dataClass.TodoCategory
import com.example.mytodolist.database.dataClass.TodoDataClass


@Database(entities = [TodoDataClass::class,TodoCategory::class],
    version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun todoCategoryDao(): TodoCategoryDao
}