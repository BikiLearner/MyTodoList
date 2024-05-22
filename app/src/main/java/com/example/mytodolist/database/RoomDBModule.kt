package com.example.mytodolist.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RoomDBModule {
    @Singleton
    @Provides
    fun provideTodoDatabase(@ApplicationContext context: Context):TodoDatabase{
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_database"
        ).fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    @Singleton
    fun provideTodoDao(database: TodoDatabase) = database.todoDao()
    @Provides
    @Singleton
    fun provideTodoCategoryDao(database: TodoDatabase) = database.todoCategoryDao()
}