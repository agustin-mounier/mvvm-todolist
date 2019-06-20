package com.example.viemedtodolist.data

import java.io.Serializable

data class Task (
    val id: String,
    var name: String,
    var note: String?,
    var isDone: Boolean
) : Serializable