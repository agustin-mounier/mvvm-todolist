package com.example.viemedtodolist.networking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.viemedtodolist.data.TasksRepository

class TasksService {

    private val LOG_TAG = "Request exception"
    private var isLoading = MutableLiveData<Boolean>()
    private var requestErrorAction = MutableLiveData<RequestErrorAction>()

    init {
        requestErrorAction.value = RequestErrorAction.NONE
    }

    fun isLoading() = isLoading as LiveData<Boolean>

    fun getNetworkError() = requestErrorAction as LiveData<RequestErrorAction>

    fun createTask(name: String, note: String, isDone: Boolean) {
        isLoading.value = true
        VMApolloClient.createTask(name, note, isDone) { task, exception ->
            isLoading.postValue(false)
            if (task != null) {
                TasksRepository.addTask(task)
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
                TasksRepository.removeTask(id)
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
                TasksRepository.setTasks(tasks)
            }
            isLoading.postValue(false)
        }
    }

    fun toggleCheck(id: String, isDone: Boolean) {
        VMApolloClient.updateTaskStatus(id, isDone) { isDone, exception ->
            if (exception == null) {
                TasksRepository.setIsDone(id, isDone!!)
            } else {
                Log.d(LOG_TAG, exception.toString())
                requestErrorAction.postValue(RequestErrorAction.UPDATE)
            }
        }
    }

    


}