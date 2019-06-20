package com.example.viemedtodolist.viewmodels

import androidx.lifecycle.ViewModel
import com.example.viemedtodolist.data.Task
import com.example.viemedtodolist.data.TasksRepository


class TasksViewModel : ViewModel() {

    fun getTasks() = TasksRepository.getTasks()

    fun getResquestErrorAction() = TasksRepository.getNetworkError()
}