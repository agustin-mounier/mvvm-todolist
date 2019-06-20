package com.example.viemedtodolist.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.viemedtodolist.networking.RequestErrorAction
import com.example.viemedtodolist.networking.VMApolloClient

/**
 * Tasks repository that acts as a single source of truth for all the app data.
 * It doesn't persists the data as its easily obtainable for the scope of this sample project,
 * however if you'd like to see a Room implementation for in phone persistence I'm happy to do it.
 */
object TasksRepository {

    private var taskList = mutableListOf<Task>()
    private val tasks = MutableLiveData<MutableList<Task>>()

    private var isLoading = MutableLiveData<Boolean>()
    private var requestErrorAction = MutableLiveData<RequestErrorAction>()

    private val LOG_TAG = "Request exception"

    init {
        tasks.value = taskList
        requestErrorAction.value = RequestErrorAction.NONE
    }

    fun isLoading() = isLoading as LiveData<Boolean>

    fun getTasks() = tasks as LiveData<List<Task>> //Cast to prevent modifications

    fun getNetworkError() = requestErrorAction as LiveData<RequestErrorAction>

    fun addTask(task: Task) {
        taskList.add(task)
        tasks.postValue(taskList)
    }

    fun removeTask(id: String) {
        val task = taskList.find { it.id == id }
        taskList.remove(task)
        tasks.postValue(taskList)
    }

    fun setTasks(tasks: List<Task>) {
        taskList = tasks as MutableList<Task>
        this.tasks.postValue(taskList)
    }

    fun toggleCheck(id: String) {
        val task = taskList.find { task -> task.id == id }!!
        task.isDone = !task.isDone
        tasks.value = taskList
        VMApolloClient.updateTaskStatus(id, task.isDone) { _, exception ->
            exception?.let {
                Log.d(LOG_TAG, it.toString())
                requestErrorAction.postValue(RequestErrorAction.UPDATE)
            }
        }
    }

    fun createTask(name: String, note: String, isDone: Boolean) {
        isLoading.value = true
        VMApolloClient.createTask(name, note, isDone) { task, exception ->
            isLoading.postValue(false)
            if (task != null) {
                addTask(task)
            } else {
                requestErrorAction.postValue(RequestErrorAction.CREATE)
            }

            exception?.let { Log.d(LOG_TAG, it.toString()) }
        }
    }

    fun deleteTask(id: String) {
        isLoading.value = true
        VMApolloClient.deleteTask(id) { deleteTask, exception ->
            if (deleteTask != null && deleteTask) {
                removeTask(id)
            } else {
                requestErrorAction.postValue(RequestErrorAction.DELETE)
            }

            exception?.let { Log.d(LOG_TAG, it.toString()) }
        }
    }

    fun fetchAllTasks() {
        isLoading.value = true
        VMApolloClient.getAllTasks { tasks, exception ->
            if (tasks != null) {
                setTasks(tasks)
            }
            isLoading.postValue(false)
        }
    }
}