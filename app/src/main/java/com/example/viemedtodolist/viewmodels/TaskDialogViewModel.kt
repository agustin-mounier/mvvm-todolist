package com.example.viemedtodolist.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.viemedtodolist.data.TasksRepository

class TaskDialogViewModel: ViewModel() {

    private val createTaskStatus = MutableLiveData<String>()

    fun createTask(name: String, note: String, isDone: Boolean) {
        if (name.isNullOrEmpty()) {
            createTaskStatus.value = "Task name is mandatory"
        } else {
            TasksRepository.createTask(name, note, isDone)
        }
    }

    fun deleteTask(id: String) {
        TasksRepository.deleteTask(id)
    }

    fun getCreateTaskStatus() = createTaskStatus as LiveData<String>
}