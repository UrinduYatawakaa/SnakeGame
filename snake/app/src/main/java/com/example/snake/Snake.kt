package com.example.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snake.ui.theme.SnakeTheme

class Snake : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SnakeTheme {
                val viewModel = viewModel<SnakeViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()
                SnakeScreen(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }

    private fun saveData() {



    }
}
