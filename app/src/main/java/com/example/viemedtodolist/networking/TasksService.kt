package com.example.viemedtodolist.networking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.viemedtodolist.AllTasksQuery
import com.example.viemedtodolist.CreateTaskMutation
import com.example.viemedtodolist.DeleteTaskMutation
import com.example.viemedtodolist.UpdateTaskStatusMutation
import com.example.viemedtodolist.data.TasksRepository

class TasksService {

    private val LOG_TAG = "Request exception"
    private val isLoading = MutableLiveData<Boolean>()
    private var requestErrorAction = MutableLiveData<RequestAction>()

    init {
        requestErrorAction.value = RequestAction.NONE
    }

    fun isLoading() = isLoading as LiveData<Boolean>

    fun getNetworkError() = requestErrorAction as LiveData<RequestAction>

    fun createTask(name: String, note: String, isDone: Boolean) {
        val callback = TaskCallback<CreateTaskMutation.Data>(RequestAction.CREATE) {
            val task = VMApolloClient.transformToTask(it?.createTask()!!)
            TasksRepository.addTask(task)
        }
        VMApolloClient.createTask(name, note, isDone, callback)
    }

    fun deleteTask(id: String) {
        val callback = TaskCallback<DeleteTaskMutation.Data>(RequestAction.DELETE) {
            if (it?.deleteTask() == true) TasksRepository.removeTask(id)
        }
        VMApolloClient.deleteTask(id, callback)
    }

    fun fetchAllTasks() {
        val callback = TaskCallback<AllTasksQuery.Data>(RequestAction.NONE) {
            val tasks = VMApolloClient.transformToTaskList(it?.allTasks()!!)
            TasksRepository.setTasks(tasks)
        }
        VMApolloClient.getAllTasks(callback)
    }

    fun toggleCheck(id: String, isDone: Boolean) {
        val callback = TaskCallback<UpdateTaskStatusMutation.Data>(RequestAction.UPDATE) {
            TasksRepository.setIsDone(id, it?.updateTaskStatus()?.isDone!!)
        }
        VMApolloClient.updateTaskStatus(id, isDone, callback)
    }

    /**
     * OnFailure callback is the same for all requests.
     */
    inner class TaskCallback<T>(
        private val requestAction: RequestAction,
        private val onSuccessFun: (T?) -> Unit
    ) : ApolloCall.Callback<T>() {

        init {
            isLoading.postValue(true)
        }

        override fun onResponse(response: Response<T>) {
            onSuccessFun(response.data())
            isLoading.postValue(false)
        }

        override fun onFailure(e: ApolloException) {
            Log.d(LOG_TAG, e.toString())
            requestErrorAction.postValue(requestAction)
            isLoading.postValue(false)
        }
    }


}