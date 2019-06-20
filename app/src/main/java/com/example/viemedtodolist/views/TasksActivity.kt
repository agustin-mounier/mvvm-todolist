package com.example.viemedtodolist.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viemedtodolist.*
import com.example.viemedtodolist.data.Task
import com.example.viemedtodolist.networking.RequestErrorAction
import com.example.viemedtodolist.viewmodels.TasksViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class TasksActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(TasksViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setUpTasksView()

        setTasksObserver()
        setRequestErrorObserver()
        new_task.setOnClickListener { view ->
            AddTaskDialog.show(supportFragmentManager)
        }
    }

    private fun setTasksObserver() {
        viewModel.getTasks().observe(this, Observer<List<Task>> { tasks ->
            if (tasks.isNullOrEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                setTasks(tasks)
            }
        })
    }

    private fun setRequestErrorObserver() {
        viewModel.getResquestErrorAction().observe(this, Observer { requestErrorAction ->
            var errorMessage: String? = null
            when(requestErrorAction) {
                RequestErrorAction.DELETE -> errorMessage = "Error deleting task"
                RequestErrorAction.CREATE -> errorMessage = "Error creating task"
                RequestErrorAction.UPDATE -> errorMessage = "Error updating task"
            }
            errorMessage?.let { Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show() }
        })
    }

    private fun setUpTasksView() {
        tasks_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        tasks_view.adapter = TasksAdapter(supportFragmentManager)
    }

    fun setTasks(tasks: List<Task>) {
        val tasksAdapter = tasks_view.adapter as TasksAdapter
        tasksAdapter.setTasks(tasks)
        tasksAdapter.notifyDataSetChanged()
    }

    fun hideEmptyState() {
        thumbs_up_icon.visibility = View.GONE
        empty_state_text.visibility = View.GONE
        tasks_view.visibility = View.VISIBLE
    }

    fun showEmptyState() {
        thumbs_up_icon.visibility = View.VISIBLE
        empty_state_text.visibility = View.VISIBLE
        tasks_view.visibility = View.GONE
    }
}
