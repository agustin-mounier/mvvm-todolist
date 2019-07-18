package com.example.viemedtodolist.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Tasks Data Access Object. This is where the database CRUD (create, read, update and delete) operations are defined.
 */
@Dao
interface TaskDAO {

    @Insert
    fun insert(task: Task)

    @Delete
    fun delete(task: Task)

    @Update
    fun updateTask(task: Task)

    @Query("SELECT * FROM tasks_table")
    fun getAllTasks(): LiveData<List<Task>>
}