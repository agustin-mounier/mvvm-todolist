package com.example.viemedtodolist.views

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viemedtodolist.data.Task
import com.example.viemedtodolist.data.TasksRepository
import kotlinx.android.synthetic.main.task_row.view.*

class TaskViewHolder(itemView: View, private val fragmentManager: FragmentManager) : RecyclerView.ViewHolder(itemView) {

    fun bind(task: Task) {
        itemView.task_name.text = task.name
        itemView.task_note.text = task.note
        itemView.task_check_box.isChecked = task.isDone

        itemView.task_check_box.setOnClickListener {
            TasksRepository.toggleCheck(task.id)
        }
        itemView.setOnClickListener {
            DeleteTaskDialog.show(fragmentManager, task)
        }
    }


}