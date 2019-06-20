package com.example.viemedtodolist.views

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskSeparatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(text: String) {
        (itemView as TextView).text = text
    }
}