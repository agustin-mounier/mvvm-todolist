package com.example.viemedtodolist.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.viemedtodolist.R
import com.example.viemedtodolist.data.Task
import kotlinx.android.synthetic.main.task_dialog_delete_layout.*

class DeleteTaskDialog : TaskDialog() {

    private lateinit var task: Task

    companion object {
        private const val TASK_KEY = "task"

        fun show(fragmentManager: FragmentManager, task: Task) {
            val deleteTaskDialog = DeleteTaskDialog()
            val args = Bundle()
            args.putSerializable(TASK_KEY, task)
            deleteTaskDialog.arguments = args
            deleteTaskDialog.show(fragmentManager, TAG)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        task = arguments?.getSerializable(TASK_KEY) as Task
        return inflater.inflate(R.layout.task_dialog_delete_layout, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        task_dialog_delete_checkbox.isChecked = task.isDone
        task_dialog_delete_name.text = task.name
        task_dialog_delete_notes.text = task.note

        task_dialog_remove.setOnClickListener {
            viewModel.deleteTask(task.id)
            dismiss()
        }
        task_dialog_delete_close.setOnClickListener { dismiss() }

    }
}