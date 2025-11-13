package com.example.copilotexercise

sealed class SnakeGameIntent {
    object Tick : SnakeGameIntent()
    data class ChangeDirection(val direction: Direction) : SnakeGameIntent()
    object Restart : SnakeGameIntent()
    object Pause : SnakeGameIntent()
    object Start : SnakeGameIntent()
}
