package com.example.viemedtodolist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1)
abstract class TasksDataBase : RoomDatabase() {

    abstract fun taskDao(): TaskDAO

    companion object {
        private const val databaseName = "tasks_database"
        private val lock = Object()

        private var instance: TasksDataBase? = null

        fun getInstance(context: Context): TasksDataBase? {
            if (instance == null) {

                synchronized(lock) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TasksDataBase::class.java, databaseName
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return instance
        }
    }

}