package com.example.viemedtodolist.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.viemedtodolist.networking.TasksService

/**
 * Tasks repository that acts as a single source of truth for all the app data.
 * It doesn't persists the data as its easily obtainable for the scope of this sample project,
 * however there is a room implementation of the repository in class TasksLocalRepository.
 */
object TasksRepository {

    private var taskList = mutableListOf<Task>()
    private val tasks = MutableLiveData<MutableList<Task>>()
    private val tasksService = TasksService()

    init {
        tasks.value = taskList
    }

    fun isLoading() = tasksService.isLoading()

    fun getTasks() = tasks as LiveData<List<Task>> //Cast to prevent modifications

    fun getNetworkError() = tasksService.getNetworkError()

    fun createTask(name: String, note: String, isDone: Boolean) {
        tasksService.createTask(name, note, isDone)
    }

    fun deleteTask(id: String) {
        tasksService.deleteTask(id)
    }

    fun toggleCheck(id: String) {
        val task = taskList.find { task -> task.id == id }!!
        tasksService.toggleCheck(id, !task.isDone)
    }

    fun fetchAllTasks() {
        tasksService.fetchAllTasks()
    }

    fun addTask(task: Task) {
        taskList.add(task)
        tasks.postValue(taskList)
    }

    fun removeTask(id: String) {
        val task = taskList.find { it.id == id }
        taskList.remove(task)
        tasks.postValue(taskList)
    }

    fun setTasks(newTasks: List<Task>) {
        taskList = newTasks as MutableList<Task>
        tasks.postValue(taskList)
    }

    fun setIsDone(id: String, isDone: Boolean) {
        val task = taskList.find { task -> task.id == id }!!
        task.isDone = isDone
        tasks.postValue(taskList)
    }
}