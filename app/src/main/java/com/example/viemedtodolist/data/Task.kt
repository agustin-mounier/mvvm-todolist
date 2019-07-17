package com.example.viemedtodolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tasks_table")
data class Task (
    val id: String,
    var name: String,
    var note: String?,
    var isDone: Boolean
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var tableId: Long = 0
}