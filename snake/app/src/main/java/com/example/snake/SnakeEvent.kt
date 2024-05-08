package com.example.snake

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset

sealed class SnakeEvent {
    data object StartGame : SnakeEvent()
    data object PauseGame : SnakeEvent()
    data object ResetGame : SnakeEvent()
    data class UpdateDirection(val offset: Offset, val canvasWidth: Int) : SnakeEvent()
}