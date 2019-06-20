package com.example.viemedtodolist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.viemedtodolist.data.Task
import com.example.viemedtodolist.data.TasksRepository
import com.example.viemedtodolist.views.TasksActivity
import com.example.viemedtodolist.views.TasksAdapter
import kotlinx.android.synthetic.main.content_main.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TaskActivityTest {

    private lateinit var activity: TasksActivity
    private val taskAdapter = mock(TasksAdapter::class.java)

    @Before
    fun setUp() {
        activity = spy(Robolectric.buildActivity(TasksActivity::class.java).get())
        val recyclerView = mock(RecyclerView::class.java)
        `when`(recyclerView.adapter).thenReturn(taskAdapter)
        `when`(activity.tasks_view).thenReturn(recyclerView)
        `when`(activity.thumbs_up_icon).thenReturn(mock(ImageView::class.java))
        `when`(activity.empty_state_text).thenReturn(mock(TextView::class.java))
    }

    @Test
    fun testSetTasks() {
        val taskList = mock(List::class.java) as List<Task>

        activity.setTasks(taskList)

        verify(taskAdapter).setTasks(taskList)
        verify(taskAdapter).notifyDataSetChanged()
    }

    @Test
    fun testHideEmptyState() {
        activity.hideEmptyState()

        verify(activity.thumbs_up_icon).visibility = View.GONE
        verify(activity.empty_state_text).visibility = View.GONE
        verify(activity.tasks_view).visibility = View.VISIBLE
    }

    @Test
    fun testShowEmptyState() {
        activity.showEmptyState()

        verify(activity.thumbs_up_icon).visibility = View.VISIBLE
        verify(activity.empty_state_text).visibility = View.VISIBLE
        verify(activity.tasks_view).visibility = View.GONE
    }

    @Test
    fun testSetUpTasksObserver() {
        TasksRepository.addTask(mock(Task::class.java))

        verify(activity).setTasks(ArgumentMatchers.anyList())
    }
}