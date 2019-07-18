package com.example.viemedtodolist.data

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

class TasksLocalRepository(application: Application) {

    private val tasksDAO: TaskDAO
    private val tasks: LiveData<List<Task>>

    init {
        val dataBase = TasksDataBase.getInstance(application.applicationContext)!!
        tasksDAO = dataBase.taskDao()

        tasks = tasksDAO.getAllTasks()
    }

    fun getTasks() = tasks

    fun createTask(name: String, note: String, isDone: Boolean) {
        val task = Task("", name, note, isDone)
        InsertTaskAsync(tasksDAO).execute(task)
    }

    fun deleteTask(task: Task) {
        DeleteTaskAsync(tasksDAO).execute(task)
    }

    fun updateTask(task: Task) {
        UpdateTaskAsync(tasksDAO).execute(task)
    }

    private class InsertTaskAsync(val taskDAO: TaskDAO) : AsyncTask<Task, Unit, Unit>() {
        override fun doInBackground(vararg p0: Task?) {
            taskDAO.insert(p0[0]!!)
        }
    }

    private class DeleteTaskAsync(val taskDAO: TaskDAO) : AsyncTask<Task, Unit, Unit>() {
        override fun doInBackground(vararg p0: Task?) {
            taskDAO.delete(p0[0]!!)
        }
    }

    private class UpdateTaskAsync(val taskDAO: TaskDAO) : AsyncTask<Task, Unit, Unit>() {
        override fun doInBackground(vararg p0: Task?) {
            taskDAO.updateTask(p0[0]!!)
        }
    }
}