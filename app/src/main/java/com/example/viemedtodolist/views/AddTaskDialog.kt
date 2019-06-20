package com.example.viemedtodolist.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.example.viemedtodolist.R
import kotlinx.android.synthetic.main.task_dialog_add_layout.*

class AddTaskDialog : TaskDialog() {

    companion object {
        fun show(fragmentManager: FragmentManager) {
            AddTaskDialog().show(fragmentManager, TAG)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.task_dialog_add_layout, container)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setErrorObserver()
        task_dialog_add_task.setOnClickListener {
            val taskName = task_dialog_add_name.text.toString()
            val taskNote = task_dialog_add_notes.text.toString()
            val isDone = task_dialog_add_checkbox.isChecked
            viewModel.createTask(taskName, taskNote, isDone)
            dismiss()
        }

        task_dialog_add_close.setOnClickListener { dismiss() }
    }

    fun setErrorObserver() {
        viewModel.getCreateTaskStatus().observe(this,  Observer { taskStatus ->
            Toast.makeText(context, taskStatus, Toast.LENGTH_SHORT).show()
        })
    }
}
