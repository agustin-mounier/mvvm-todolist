package com.example.viemedtodolist.views

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.viemedtodolist.viewmodels.TaskDialogViewModel


abstract class TaskDialog : DialogFragment() {
    companion object {
        const val TAG = "TASK_DIALOG"
    }

    protected val viewModel by lazy { ViewModelProviders.of(this).get(TaskDialogViewModel::class.java) }

    // Dialog fragments are only a wrapper to hold a dialog.
    // So it is the window, not the container, that needs to be adjusted at run time.
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window.setLayout(width, height)
        }
    }
}
