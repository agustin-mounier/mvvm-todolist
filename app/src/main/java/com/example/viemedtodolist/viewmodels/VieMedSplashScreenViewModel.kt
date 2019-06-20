package com.example.viemedtodolist.viewmodels

import androidx.lifecycle.ViewModel
import com.example.viemedtodolist.data.TasksRepository

class VieMedSplashScreenViewModel: ViewModel() {
    
    init {
        TasksRepository.fetchAllTasks()
    }

    fun isLoading() = TasksRepository.isLoading()
}