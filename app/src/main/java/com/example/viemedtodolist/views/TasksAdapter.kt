package com.example.viemedtodolist.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.viemedtodolist.R
import com.example.viemedtodolist.data.Task

class TasksAdapter(private val fragmentManager: FragmentManager) : Adapter<ViewHolder>() {

    companion object {
        private const val TASK_TYPE = 1
        private const val TASK_SEPARATOR_TYPE = 2

        private const val COMPLETED_TASK_SEPARATOR = "ALREADY DONE"
        private const val MY_TASK_SEPARATOR = "MY TASKS FOR TODAY"
    }

    private lateinit var todayTasks: ArrayList<Task>
    private lateinit var alreadyDoneTasks: ArrayList<Task>

    private var indexOffset = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            TASK_TYPE -> {
                val taskRowView = layoutInflater.inflate(R.layout.task_row, parent, false)
                TaskViewHolder(taskRowView, fragmentManager)
            }
            else -> {
                val taskSeparatorView = layoutInflater.inflate(R.layout.task_separator, parent, false)
                TaskSeparatorViewHolder(taskSeparatorView)
            }
        }
    }

    //We manage the tasks separators here in the adapter so we need to add 1 to the item count if we are going to put the corresponding separator
    override fun getItemCount(): Int {
        var totalItemCount = 0
        totalItemCount += if (todayTasks.size > 0) todayTasks.size + 1 else 0
        totalItemCount += if (alreadyDoneTasks.size > 0) alreadyDoneTasks.size + 1 else 0
        return totalItemCount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (shouldInsertMyTaskSeparator(position)) {
            (holder as TaskSeparatorViewHolder).bind(MY_TASK_SEPARATOR)
            indexOffset += 1
        } else if (shouldInsertCompletedSeparator(position)) {
            (holder as TaskSeparatorViewHolder).bind(COMPLETED_TASK_SEPARATOR)
            indexOffset += 1
        } else {
            val taskViewHolder = holder as TaskViewHolder
            val actualPosition = position - indexOffset
            if (actualPosition < todayTasks.size) {
                taskViewHolder.bind(todayTasks[actualPosition])
            } else {
                taskViewHolder.bind(alreadyDoneTasks[actualPosition - todayTasks.size])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (shouldInsertMyTaskSeparator(position) || shouldInsertCompletedSeparator(position)) {
            return TASK_SEPARATOR_TYPE
        }
        return TASK_TYPE
    }

    fun setTasks(tasks: List<Task>) {
        todayTasks = ArrayList()
        alreadyDoneTasks = ArrayList()
        indexOffset = 0
        tasks.forEach { task ->
            if (task.isDone) {
                alreadyDoneTasks.add(task)
            } else {
                todayTasks.add(task)
            }
        }
    }

    /**
     * If we are on the first position of the list and have uncompleted task
     * then we should insert the "MY TASKS FOR TODAY" separator
     */
    private fun shouldInsertMyTaskSeparator(position: Int) : Boolean {
        return position == 0 && todayTasks.size > 0
    }

    /**
     * If we are on the first position of the list or at the end of the "to do" todayTasks
     * and have completed todayTasks then we should insert the "ALREADY DONE" separator
     */
    private fun shouldInsertCompletedSeparator(position: Int) : Boolean {
        if (position == 0 && todayTasks.size == 0 && alreadyDoneTasks.size > 0)
            return true
        if (todayTasks.size > 0 && position == todayTasks.size + 1 && alreadyDoneTasks.size > 0)
            return true
        return false
    }
}