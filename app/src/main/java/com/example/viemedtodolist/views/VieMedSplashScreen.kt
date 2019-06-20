package com.example.viemedtodolist.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.viemedtodolist.R
import com.example.viemedtodolist.viewmodels.VieMedSplashScreenViewModel

class VieMedSplashScreen : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(VieMedSplashScreenViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_layout)

        viewModel.isLoading().observe(this, Observer<Boolean> { loading ->
            if(loading != null && !loading) {
                val tasksActivityIntent = Intent(this, TasksActivity::class.java)
                startActivity(tasksActivityIntent)
            }
        })
    }
}